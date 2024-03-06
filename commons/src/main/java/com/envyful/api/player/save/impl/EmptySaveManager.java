package com.envyful.api.player.save.impl;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.database.Database;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.save.AbstractSaveManager;
import com.google.common.base.Preconditions;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class EmptySaveManager<T> extends AbstractSaveManager<T> {

    public EmptySaveManager(PlayerManager<?, T> playerManager) {
        super(playerManager);
    }

    public EmptySaveManager(PlayerManager<?, T> playerManager, BiConsumer<EnvyPlayer<T>, Throwable> errorHandler) {
        super(playerManager, errorHandler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Attribute<B, T>, B> CompletableFuture<A> loadAttribute(Class<? extends A> attributeClass, B id) {
        Preconditions.checkNotNull(attributeClass, "Cannot load attribute with null class");
        Preconditions.checkNotNull(id, "Cannot load attribute with null id");

        return CompletableFuture.supplyAsync(() -> {
                    var data = this.registeredAttributes.get(attributeClass);

                    if (data.shared()) {
                        A sharedAttribute = (A) this.getSharedAttribute(attributeClass, id);

                        if (sharedAttribute == null) {
                            sharedAttribute = (A) data.constructor().get();
                            sharedAttribute.load(id);
                            this.addSharedAttribute(id, sharedAttribute);
                        }

                        return sharedAttribute;
                    } else {
                        A attribute = (A) data.constructor().get();
                        attribute.load(id);
                        return attribute;
                    }
                }, UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE)
                .exceptionally(throwable -> {
                    UtilLogger.logger().ifPresent(logger -> logger.error("Error when loading attribute data for " + attributeClass.getName(), throwable));
                    return null;
                });
    }

    @Override
    public <A> void saveData(A id, Attribute<A, T> attribute) {
        attribute.save(id);
    }

    @Override
    public boolean delete(String name) {
        return false;
    }

    @Override
    public boolean delete(Database database, String name) {
        return false;
    }
}
