package com.envyful.api.player.attribute;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.platform.Messageable;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.parse.SimplePlaceholder;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 *
 * Class representing an attribute of a player that is potentially offline
 *
 * @param <A> The type of the attribute id
 * @param <B> The type of the attribute
 */
public class OfflineAttribute<A, B extends Attribute<A>> implements SimplePlaceholder, Messageable<EnvyPlayer<?>> {

    private final Class<B> attributeClass;
    private final UUID uuid;
    private final String name;

    private EnvyPlayer<?> cachedPlayer;
    private A id;
    private B cachedAttribute;
    private CompletableFuture<B> loadingAttribute;

    private OfflineAttribute(Class<B> attributeClass, UUID uuid, String name) {
        this.attributeClass = attributeClass;
        this.uuid = uuid;
        this.name = name;

        this.id = PlatformProxy.getPlayerManager().mapId(attributeClass, this.uuid);
        this.loadingAttribute = PlatformProxy.getPlayerManager().loadAttribute(attributeClass, this.id).thenApply(b -> this.cachedAttribute = b);
    }

    private OfflineAttribute(Class<B> attributeClass, EnvyPlayer<?> player) {
        this.attributeClass = attributeClass;
        this.uuid = player.getUniqueId();
        this.name = player.getName();

        this.cachedPlayer = player;
        this.cachedAttribute = player.getAttributeNow(attributeClass);
        this.id = cachedAttribute.getId();
    }

    /**
     *
     * Gets the id of the attribute
     *
     * @return The id
     */
    public A getId() {
        return this.id;
    }

    /**
     *
     * Gets the name of the player
     *
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * Gets the unique id of the player
     *
     * @return The unique id
     */
    public UUID getUniqueId() {
        return this.uuid;
    }

    /**
     *
     * Gets the attribute for the player
     *
     * @return The attribute
     */
    public B getAttribute() {
        if (this.cachedAttribute != null) {
            return this.cachedAttribute;
        }

        if (this.getParent() != null) {
            var attribute = this.getParent().getAttributeNow(this.attributeClass);
            this.cachedAttribute = attribute;
            return attribute;
        }

        if (this.loadingAttribute != null) {
            return this.loadingAttribute.join();
        }

        return null;
    }

    /**
     *
     * Saves the attribute data if the player is offline
     *
     */
    public void save() {
        if (this.getParent() != null) {
            return;
        }

        UtilConcurrency.runAsync(() -> this.getAttribute().save(this.id));
    }


    @Override
    public void message(Object... objects) {
        if (this.getParent() == null) {
            return;
        }

        this.getParent().message(objects);
    }

    @Override
    public void message(Collection<String> messages, Placeholder... placeholders) {
        if (this.getParent() == null) {
            return;
        }

        PlatformProxy.sendMessage(this.getParent(), messages, placeholders);
    }

    @Nullable
    @Override
    public EnvyPlayer<?> getParent() {
        if (this.cachedPlayer == null) {
            this.cachedPlayer = (EnvyPlayer<?>) PlatformProxy.getPlayerManager().getPlayer(this.uuid);
        }

        return this.cachedPlayer;
    }

    @Override
    public String replace(String s) {
        if (this.getParent() != null) {
            return this.getParent().replace(s);
        }

        return s.replace("%player%", this.name);
    }

    /**
     *
     * Attempts to load an attribute for a (potentially) offline player based on their username
     * <br>
     * Remember that this method is blocking and should be run asynchronously
     * <br>
     * Also, important to note that if you modify the attribute and the player is offline it will not be saved unless
     * you explicitly call {@link OfflineAttribute#save()}
     *
     * @param attributeClass The class of the attribute
     * @param name The name of the player
     * @return The attribute
     * @param <X> The type of the player
     * @param <Y> The type of the attribute class
     */
    @SuppressWarnings("unchecked")
    public static <X, Y extends Attribute<X>> OfflineAttribute<X, Y> fromName(Class<Y> attributeClass, String name) {
        var player = PlatformProxy.getPlayerManager().getOnlinePlayer(name);

        if (player != null) {
            return new OfflineAttribute<>(attributeClass, player);
        }

        var uuid = PlatformProxy.getPlayerManager().getNameStore().getUUID(name).join();

        if (uuid == null) {
            return null;
        }

        return new OfflineAttribute<>(attributeClass, uuid, name);
    }
}
