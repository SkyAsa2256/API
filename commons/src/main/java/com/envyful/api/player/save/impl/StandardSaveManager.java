package com.envyful.api.player.save.impl;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.save.AbstractSaveManager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class StandardSaveManager<T extends AttributeHolder> extends AbstractSaveManager<T> {

    public StandardSaveManager() {
        super();
    }

    public StandardSaveManager(BiConsumer<T, Throwable> errorHandler) {
        super(errorHandler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Attribute> CompletableFuture<A> loadAttribute(Class<? extends A> attributeClass, UUID id) {
        assert attributeClass != null : "Cannot load attribute with null class";
        assert id != null : "Cannot load attribute with null id";

        return CompletableFuture.supplyAsync(() -> {
                    var data = this.registeredAttributes.get(attributeClass);

                    if (data.shared()) {
                        A sharedAttribute = (A) this.getSharedAttribute(attributeClass, id);

                        if (sharedAttribute == null) {
                            sharedAttribute = this.loadAttributeFromDataGeneric(data, id);
                            this.addSharedAttribute(id, sharedAttribute);
                        }

                        return sharedAttribute;
                    } else {
                        return this.loadAttributeFromDataGeneric(data, id);
                    }
                }, UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE)
                .exceptionally(throwable -> {
                    UtilLogger.logger().ifPresent(logger -> logger.error("Error when loading attribute data for " + attributeClass.getName(), throwable));
                    return null;
                });
    }

    @Override
    public void saveData(Attribute attribute) {
        var data = this.registeredAttributes.get(attribute.getClass());

        if (data == null) {
            return;
        }

        this.saveAttributeFromDataGeneric(data, attribute);
    }
}
