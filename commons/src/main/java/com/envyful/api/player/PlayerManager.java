package com.envyful.api.player;

import com.envyful.api.player.attribute.data.AttributeData;
import com.envyful.api.player.name.NameStore;
import com.envyful.api.player.save.SaveManager;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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
      * Sets the player manager's {@link SaveManager}
      *
      * @param saveManager The new save manager
      */
     void setSaveManager(SaveManager<A> saveManager);

     /**
      *
      * Gets the backing save manager
      *
      * @return The save manager
      */
     SaveManager<A> getSaveManager();

     /**
      *
      * Gets the name store for the player manager
      *
      * @return The name store
      */
     @Nullable
     NameStore getNameStore();

     /**
      *
      * Sets the name store for the player manager
      *
      * @param nameStore The new name store
      */
     void setNameStore(NameStore nameStore);

     /**
      *
      * Loads the data for a single attribute using the given id
      *
      * @param attributeClass The class of the attribute
      * @param id The id to load the data using
      * @return The attribute instance
      * @param <X> The attribute type
      */
     default <X extends Attribute> CompletableFuture<X> loadAttribute(Class<? extends X> attributeClass, UUID id) {
          return this.getSaveManager().loadAttribute(attributeClass, id);
     }

     /**
      *
      * Registers an {@link com.envyful.api.player.attribute.PlayerAttribute} class so that when the player object is
      * instantiated it can be created (using reflection) from the registry in the PlayerManager implementation.
      * <br>
      * If {@link PlayerManager#setSaveManager(SaveManager)} has been called then it will call
      * {@link SaveManager#registerAttribute(AttributeData)}  on the given class
      *
      * @param attribute The class of the attribute being registered
      */
     default <X extends Attribute> void registerAttribute(Class<X> attribute, Function<UUID, X> constructor) {
          this.registerAttribute(Attribute.<X, A>builder(attribute).constructor(constructor));
     }

     /**
      *
      * Creates a new {@link AttributeBuilder} for the given attribute class
      *
      * @param attributeClass The class of the attribute
      * @param <A> The attribute type
      * @return The attribute builder
      */
     default <A extends Attribute> AttributeBuilder<A, EnvyPlayer<B>> builder(Class<A> attributeClass) {
          return Attribute.builder(attributeClass);
     }

     /**
      *
      * Registers an {@link com.envyful.api.player.attribute.PlayerAttribute} class so that when the player object is
      * instantiated it can be created (using reflection) from the registry in the PlayerManager implementation.
      * <br>
      * If {@link PlayerManager#setSaveManager(SaveManager)} has been called then it will call
      * {@link SaveManager#registerAttribute(AttributeData)} on the given class
      *
      * @param builder The builder for the attribute
      * @param <X> The attribute type
      */
     default <X extends Attribute> void registerAttribute(AttributeBuilder<X, A> builder) {
          var data = new AttributeData<>(
                  builder.attributeClass(),
                  builder.isShared(),
                  builder.constructor,
                  builder.idMapper,
                  builder.predicates,
                  builder.triggers,
                  builder.offlineIdMapper,
                  this.getSaveManager(),
                  builder.registeredAdapters
          );

          this.registerAttribute(data);

          if (builder.overrideSaveMode != null) {
               this.getSaveManager().overrideSaveMode(builder.attributeClass, builder.overrideSaveMode);
          }
     }

     /**
      *
      * Maps the given uuid to the id for the attribute
      *
      * @param attributeClass The class of the attribute
      * @param uuid The id to map
      * @param <T> The attribute type
      * @return The attribute instance
      */
     <T extends Attribute> UUID mapId(Class<T> attributeClass, UUID uuid);

     /**
      *
      * Registers an {@link com.envyful.api.player.attribute.PlayerAttribute} class so that when the player object is
      * instantiated it can be created (using reflection) from the registry in the PlayerManager implementation.
      * <br>
      * If {@link PlayerManager#setSaveManager(SaveManager)} has been called then it will call
      * {@link SaveManager#registerAttribute(AttributeData)}  on the given class
      *
      * @param attributeData The data for the attribute
      * @param <X> The attribute type
      */
     <X extends Attribute> void registerAttribute(AttributeData<X, A> attributeData);

}
