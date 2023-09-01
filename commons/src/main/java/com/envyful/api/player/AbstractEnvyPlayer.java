package com.envyful.api.player;

import com.envyful.api.player.attribute.Attribute;
import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.player.save.SaveManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 *
 * This interface is designed to provide basic useful
 * methods for all the different player implementations independent
 * of the platform details (i.e. auto-translates all text sent
 * to the player, and makes it less complicated to do
 * different functions such as sending titles etc.).
 * <br>
 * It also stores {@link PlayerAttribute} from the plugin implementation
 * that will include specific data from the
 * plugin / mod. The attributes stored by the plugin's / manager's
 * class as to allow each mod / plugin to have multiple
 * attributes for storing different sets of data.
 *
 * @param <T> The specific platform implementation of the player object.
 */
public abstract class AbstractEnvyPlayer<T> implements EnvyPlayer<T> {

    protected final Map<Class<?>, Attribute<?>> attributes =
            Maps.newHashMap();

    protected final SaveManager<T> saveManager;

    protected T parent;

    protected AbstractEnvyPlayer(SaveManager<T> saveManager) {
        this.saveManager = saveManager;
    }

    @Override
    public T getParent() {
        return this.parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Attribute<B>, B> A getAttribute(Class<A> attributeClass) {
        return (A) this.attributes.get(attributeClass);
    }

    @Override
    public void invalidateAttribute(Attribute<?> attribute) {
        this.attributes.remove(attribute.getClass());
    }

    @Override
    public <A extends Attribute<B>, B> A loadAttribute(
            Class<? extends A> attributeClass, B id) {
        A loadedAttribute = this.saveManager.loadAttribute(attributeClass, id);

        this.attributes.put(
                attributeClass,
                loadedAttribute
        );

        return loadedAttribute;
    }

    @Override
    public <A extends Attribute<?>> void setAttribute(A attribute) {
        this.attributes.put(attribute.getClass(), attribute);
    }

    @Override
    public List<Attribute<?>> getAttributes() {
        return Lists.newArrayList(this.attributes.values());
    }
}
