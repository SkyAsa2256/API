package com.envyful.api.reforged.battle;

import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.enums.ExperienceGainType;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

public class BattleBuilderFactory {

    private static Map<Integer, BattleBuilder> LISTENED_CONTROLLERS = Maps.newConcurrentMap();

    static {
        Pixelmon.EVENT_BUS.register(new BattleBuilderFactory());
    }

    public static void registerBattleBuilder(BattleController battleController, BattleBuilder battleBuilder) {
        LISTENED_CONTROLLERS.put(battleController.battleIndex, battleBuilder);
    }

    @SubscribeEvent
    public void onBattleEvent(BattleStartedEvent event) {
        BattleBuilder battleBuilder = LISTENED_CONTROLLERS.get(event.bc.battleIndex);

        if (battleBuilder == null) {
            return;
        }

        battleBuilder.startConsumer.accept(event);
    }

    @SubscribeEvent
    public void onBattleEvent(BattleEndEvent event) {
        BattleBuilder battleBuilder = LISTENED_CONTROLLERS.get(event.bc.battleIndex);

        if (battleBuilder == null) {
            return;
        }

        battleBuilder.endConsumer.accept(event);
        LISTENED_CONTROLLERS.remove(event.bc.battleIndex);

        for (BattleParticipant battleParticipant : event.results.keySet()) {
            if (!(battleParticipant instanceof PlayerParticipant)) {
                continue;
            }

            ((PlayerParticipant) battleParticipant).getStorage().setInTemporaryMode(false, null);
        }
    }

    @SubscribeEvent
    public void onExpGained(ExperienceGainEvent event) {
        if (event.getType() != ExperienceGainType.BATTLE) {
            return;
        }

        if (event.pokemon.getBattleController() == null) {
            return;
        }

        BattleBuilder battleBuilder = LISTENED_CONTROLLERS.get(event.pokemon.getBattleController().battleIndex);

        if (battleBuilder.disableExp) {
            event.setCanceled(true);
            event.setExperience(0);
        }
    }
}
