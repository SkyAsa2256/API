package com.envyful.api.player.attribute;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;

import java.util.UUID;

/**
 *
 * An interface designed for storing specific data for each mod / plugin about a player.
 *
 * All implementations should stick to only keeping functions visible that operate on this object (i.e. no public getters
 * or setters) to follow SOLID rules.
 *
 * @param <A> The manager class
 */
public abstract class PlayerAttribute<A> extends AbstractAttribute<UUID, A> {

    protected final transient PlayerManager<?, ?> playerManager;

    protected transient EnvyPlayer<?> parent;

    protected PlayerAttribute(A manager, PlayerManager<?, ?> playerManager) {
        super(manager);

        this.playerManager = playerManager;
    }

    /**
     * @deprecated Use {@link Attribute#getId()}
     */
    @Deprecated
    public UUID getUuid() {
        return this.id;
    }

    @Override
    public void save(UUID id) {
        this.id = id;
        this.parent = this.playerManager.getPlayer(this.id);

        this.save(id);
    }

    @Override
    public void load(UUID id) {
        this.id = id;
        this.parent = this.playerManager.getPlayer(this.id);

        this.load(id);
    }
}
