package com.envyful.api.player;

import com.envyful.api.config.ConfigLocation;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.parse.SimplePlaceholder;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 *
 * This interface is designed to provide basic useful
 * methods for all the different player implementations independent
 * of the platform details (i.e. auto-translates
 * all text sent to the player, and makes it less complicated to do
 * different functions such as sending titles etc.).
 * <br>
 * It also stores {@link PlayerAttribute} from the
 * plugin implementation that will include specific data from the
 * plugin / mod. The attributes stored by the
 * plugin's / manager's class as to allow each mod / plugin to have multiple
 * attributes for storing different sets of data.
 *
 * @param <T> The specific platform implementation of the player object.
 */
public interface EnvyPlayer<T> extends SimplePlaceholder {

    /**
     *
     * Gets the player's UUID from Mojang
     *
     * @return The player's UUID
     */
    UUID getUniqueId();

    /**
     *
     * Gets a String representation fo the player's name
     *
     * @return The player's name
     */
    String getName();

    /**
     *
     * Gets the platform relative representation of the player
     *
     * @return The parent
     */
    @Nullable
    T getParent();

    /**
     *
     * Checks if the player has the permission
     *
     * @param permission The permission to check
     * @return If the player has the permission
     */
    default boolean hasPermission(String permission) {
        return PlatformProxy.hasPermission(this, permission);
    }

    /**
     *
     * Checks if the player is an operator
     *
     * @return If the player is an operator
     */
    default boolean isOP() {
        return PlatformProxy.isOP(this);
    }

    /**
     *
     * Sends messages to the player
     *
     * @param messages The messages to send
     * @param placeholders The placeholders to replace in the messages
     */
    default void message(Collection<String> messages, Placeholder... placeholders) {
        PlatformProxy.sendMessage(this, messages, placeholders);
    }

    /**
     *
     * Sends messages to the player
     *
     * @param messages The messages to send
     */
    void message(Object... messages);

    /**
     *
     * Sends an action bar message to the player
     *
     * @param message The message to send
     * @param placeholders The placeholders to replace in the message
     */
    void actionBar(String message, Placeholder... placeholders);

    /**
     *
     * Sends an action bar message to the player
     *
     * @param message The message to send
     */
    void actionBar(Object message);

    /**
     *
     * Execute the command as the player
     *
     * @param command The command to execute
     */
    void executeCommand(String command);

    /**
     *
     * Plays a sound to the player
     *
     * @param sound The sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(String sound, float volume, float pitch);

    /**
     *
     * Plays a sound to the player
     *
     * @param sound The sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(Object sound, float volume, float pitch);

    /**
     *
     * Execute the commands as the player
     *
     * @param commands The commands to execute
     */
    void executeCommands(String... commands);

    /**
     *
     * Teleports the player to the given location
     *
     * @param location The location to teleport the player to
     */
    void teleport(ConfigLocation location);

    /**
     *
     * Gets all the attributes for the player
     *
     * @return All the attributes
     */
    List<Attribute<?>> getAttributes();

    /**
     *
     * Get the attribute for the player
     * <br>
     * Returns a completable future that is completed once the full attribute has been loaded
     *
     * @param attributeClass The attribute class
     * @return A completable future
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    <A extends Attribute<B>, B> CompletableFuture<A> getAttribute(Class<A> attributeClass);

    /**
     *
     * Checks if the player has the attribute
     *
     * @param attributeClass The attribute class
     * @return If the player has the attribute
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    <A extends Attribute<B>, B> boolean hasAttribute(Class<A> attributeClass);

    /**
     *
     * Gets the attribute for the player immediately
     * <br>
     * NOTE: This will hold the current thread until the attribute has been loaded if it is not already
     *
     *
     * @param attributeClass The attribute class
     * @return The attribute instance
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    <A extends Attribute<B>, B> A getAttributeNow(Class<A> attributeClass);

    /**
     *
     * Sets the attribute for the player
     *
     * @param attribute The attribute to set
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    <A extends Attribute<B>, B, C extends EnvyPlayer<T>> void setAttribute(Class<A> attributeClass, CompletableFuture<A> attribute);

    /**
     *
     * Sets the attribute for the player
     *
     * @param attribute The attribute to set
     * @param <A> The attribute type
     */
    <A extends Attribute<B>, B, C extends EnvyPlayer<T>> void setAttribute(A attribute);

    /**
     *
     * Removes the attribute from the player
     *
     * @param attributeClass The attribute class
     * @param <A> The attribute type
     * @param <B> The attribute id type
     */
    <A extends Attribute<B>, B> void removeAttribute(Class<A> attributeClass);

    @Override
    default String replace(String line) {
        return line.replace("%player%", this.getName())
                .replace("%uuid%", this.getUniqueId().toString());
    }
}
