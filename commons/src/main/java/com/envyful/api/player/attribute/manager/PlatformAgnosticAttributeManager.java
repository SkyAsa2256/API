package com.envyful.api.player.attribute.manager;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.AttributeBuilder;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.data.AttributeData;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.player.save.impl.StandardSaveManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PlatformAgnosticAttributeManager<A extends AttributeHolder> implements AttributeManager<A> {

    protected final Map<Class<? extends Attribute>, AttributeData<?, A>> attributeData = new HashMap<>();
    protected final SaveManager<A> saveManager;

    public PlatformAgnosticAttributeManager(SaveManager<A> saveManager) {
        this.saveManager = saveManager;
    }

    public PlatformAgnosticAttributeManager() {
        this(new StandardSaveManager<>());
    }

    @Override
    public <X extends Attribute> CompletableFuture<X> loadAttribute(Class<? extends X> attributeClass, UUID id) {
        return this.saveManager.loadAttribute(attributeClass, id);
    }

    @Override
    public <X extends Attribute> void registerAttribute(AttributeBuilder<X, A> builder) {
        var data = new AttributeData<>(
                builder.attributeClass(),
                builder.isShared(),
                builder.constructor(),
                builder.idMapper(),
                builder.predicates(),
                builder.triggers(),
                builder.offlineIdMapper(),
                this.saveManager,
                builder.registeredAdapters()
        );

        this.registerAttribute(data);

        if (builder.overrideSaveMode() != null) {
            this.saveManager.overrideSaveMode(builder.attributeClass(), builder.overrideSaveMode());
        }
    }

    @Override
    public <T extends Attribute> UUID mapId(Class<T> attributeClass, UUID uuid) {
        var data = this.attributeData.get(attributeClass);

        if (data == null) {
            throw new IllegalArgumentException("No attribute data found for " + attributeClass.getSimpleName() + " " + this.attributeData.keySet().stream().map(Class::getSimpleName).collect(Collectors.joining(", ")));
        }

        return data.offlineIdMapper().apply(uuid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute> void registerAttribute(AttributeData<X, A> attributeData) {
        for (var trigger : attributeData.triggers()) {
            trigger.addAttribute(attributeData);
        }

        this.attributeData.put(attributeData.attributeClass(), attributeData);

        if (this.saveManager != null) {
            this.saveManager.registerAttribute(attributeData);
        }
    }
}
