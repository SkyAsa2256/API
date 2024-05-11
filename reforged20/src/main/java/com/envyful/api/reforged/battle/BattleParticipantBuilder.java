package com.envyful.api.reforged.battle;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PartyStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.awt.*;

/**
 *
 * Builder for creating a {@link BattleParticipant} instance
 *
 */
public class BattleParticipantBuilder {

    private Entity player;

    private BattleParticipantBuilder() {}

    public BattleParticipantBuilder entity(Entity player) {
        this.player = player;
        return this;
    }

    public BattleParticipantBuilder team(Pokemon... tempTeam) {
        if (!(this.player instanceof ServerPlayer)) {
            return this;
        }

        PlayerPartyStorage storage = StorageProxy.getPartyNow(this.player.getUUID());

        if (storage == null) {
            return this;
        }

        storage.setInTemporaryMode(true, Color.RED, tempTeam);

        for (int i = 0; i < tempTeam.length; i++) {
            tempTeam[i].setStorage(storage, new StoragePosition(-1, i));
        }

        return this;
    }

    public BattleParticipant build() {
        PartyStorage storage = StorageProxy.getPartyNow(this.player.getUUID());

        if (this.player instanceof ServerPlayer) {
            return new PlayerParticipant((ServerPlayer) this.player, storage.getFirstAblePokemon());
        }

        return new TrainerParticipant((NPCTrainer) this.player, null, 1);
    }

    public static BattleParticipantBuilder builder() {
        return new BattleParticipantBuilder();
    }
}
