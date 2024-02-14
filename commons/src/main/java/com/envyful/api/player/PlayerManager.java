package com.envyful.api.player;

import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.attribute.SharedAttribute;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.type.AsyncFunction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
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
      * Loads the data for a single attribute using the given id
      *
      * @param attributeClass The class of the attribute
      * @param id The id to load the data using
      * @return The attribute instance
      * @param <X> The attribute type
      * @param <Y> The id type
      */
     default <X extends Attribute<Y, B>, Y> CompletableFuture<X> loadAttribute(Class<? extends X> attributeClass, Y id) {
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
     default <X extends Attribute<Y, B>, Y> void registerAttribute(Class<X> attribute, Supplier<X> constructor) {
          this.registerAttribute(Attribute.builder(attribute).constructor(constructor));
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
      * @param <Y> The id type
      */
     default <X extends Attribute<Y, B>, Y> void registerAttribute(AttributeBuilder<X, Y, B> builder) {
          var data = new AttributeData<>(
                  builder.attributeClass,
                  SharedAttribute.class.isAssignableFrom(builder.attributeClass),
                  builder.constructor,
                  builder.idMapper,
                  builder.predicates,
                  builder.triggers,
                  this.getSaveManager()
          );

          this.registerAttribute(data);
     }

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
      * @param <Y> The id type
      */
     <X extends Attribute<Y, B>, Y> void registerAttribute(AttributeData<X, Y, B> attributeData);

     /**
      *
      * A simple data transfer object for the attribute data
      *
      * @param <A> The attribute type
      * @param <B> The id type
      * @param <C> The platform player type
      */
     class AttributeData<A extends Attribute<B, C>, B, C> {

          private final Class<A> attributeClass;
          private final boolean shared;
          private final Supplier<A> constructor;
          private final AsyncFunction<C, Object> idMapper;
          private final List<Predicate<EnvyPlayer<C>>> predicates;
          private final List<AttributeTrigger<C>> triggers;
          private final SaveManager<C> saveManager;

          protected AttributeData(Class<A> attributeClass, boolean shared, Supplier<A> constructor, AsyncFunction<C, Object> idMapper,
                                  List<Predicate<EnvyPlayer<C>>> predicates, List<AttributeTrigger<C>> triggers, SaveManager<C> saveManager) {
               this.attributeClass = attributeClass;
               this.shared = shared;
               this.constructor = constructor;
               this.idMapper = idMapper;
               this.predicates = predicates;
               this.triggers = triggers;
              this.saveManager = saveManager;
          }

          public Class<A> attributeClass() {
               return this.attributeClass;
          }

          public boolean shared() {
                 return this.shared;
            }

          public Supplier<A> constructor() {
               return this.constructor;
          }

          public AsyncFunction<C, Object> idMapper() {
               return this.idMapper;
          }

          public List<Predicate<EnvyPlayer<C>>> predicates() {
               return this.predicates;
          }

          public List<AttributeTrigger<C>> triggers() {
               return this.triggers;
          }

          public SaveManager<C> saveManager() {
                 return this.saveManager;
            }
     }
}
