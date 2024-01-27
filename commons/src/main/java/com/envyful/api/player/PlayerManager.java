package com.envyful.api.player;

import com.envyful.api.player.attribute.Attribute;
import com.envyful.api.player.save.SaveManager;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 *
 * An interface designed to store local user objects for the plugin using it.
 * Should handle all login and logout logic (reducing boilerplate / duplicate code) allowing for easy caching
 * with minimal code at the implementation level.
 * <br>
 * Check the implementation level module (i.e. forge module) for details on how to create an instance of this interface.
 *
 * @param <A> The generic for the API's player object
 * @param <B> The generic parameter for the platform's player object
 */
public interface PlayerManager<A extends EnvyPlayer<B>, B> {

     /**
      *
      * Get the {@link EnvyPlayer} implementation from the platform's player implementation
      * <br>
      * Will return null if the player is not online
      *
      * @param player The platform's player object
      * @return The API's player implementation
      */
     A getPlayer(B player);

     /**
      *
      * Get the {@link EnvyPlayer} implementation from the minecraft player's UUID
      * <br>
      * Will return null if the player is not online
      *
      * @param uuid The minecraft player's UUID
      * @return The API's player implementation
      */
     A getPlayer(UUID uuid);

     /**
      *
      * Gets the {@link EnvyPlayer} implementation from the player's username (if they are online)
      * <br>
      * Will return null if the player is not online
      *
      * @param username The username of the minecraft player
      * @return The API's player implementation
      */
     A getOnlinePlayer(String username);


     /**
      *
      * Gets the {@link EnvyPlayer} implementation from the player's username (if they are online) (case insensitive)
      * <br>
      * Will return null if the player is not online
      *
      * @param username The username of the minecraft player
      * @return The API's player implementation
      */
     A getOnlinePlayerCaseInsensitive(String username);

     /**
      *
      * Gets a {@link List} of all online players in the {@link EnvyPlayer} form
      *
      *
      * @return All online players
      */
     List<A> getOnlinePlayers();

     /**
      *
      * Gets the registered attributes for an offline player
      * Will return an empty list if the player is not found
      *
      * @param uuid The uuid of the target player
      * @return The attributes of said offline player
      */
     List<Attribute<?>> getOfflineAttributes(UUID uuid);

     /**
      *
      * Registers an {@link com.envyful.api.player.attribute.PlayerAttribute} class so that when the player object is
      * instantiated it can be created (using reflection) from the registry in the PlayerManager implementation.
      *
      * If {@link PlayerManager#setSaveManager(SaveManager)} has been called then it will call
      * {@link SaveManager#registerAttribute(Class, Supplier)}  on the given class
      *
      * @param attribute The class of the attribute being registered
      */
     <A extends Attribute<B>, B> void registerAttribute(Class<A> attribute, Supplier<A> constructor);

     /**
      *
      * Sets the player manager's {@link SaveManager}
      *
      * @param saveManager The new save manager
      */
     void setSaveManager(SaveManager<B> saveManager);

     /**
      *
      * Gets the backing save manager
      *
      * @return The save manager
      */
     SaveManager<B> getSaveManager();

     /**
      *
      * Loads attribute data for a given ID
      *
      * @param attributeClass The class being loaded
      * @param id The ID for which the data is being loaded
      * @return The loaded data
      * @param <A> The attribute type
      * @param <B> The
      */
     <A extends Attribute<B>, B> CompletableFuture<A> loadAttribute(Class<? extends A> attributeClass, B id);

}
