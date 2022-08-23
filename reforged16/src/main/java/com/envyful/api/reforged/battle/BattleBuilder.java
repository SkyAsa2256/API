package com.envyful.api.reforged.battle;

import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;

import java.util.function.Consumer;

public class BattleBuilder {

    protected BattleParticipant[] teamOne;
    protected BattleParticipant[] teamTwo;
    protected BattleRules rules;
    protected Consumer<BattleEndEvent> endConsumer;
    protected Consumer<BattleStartedEvent> startConsumer;
    protected boolean disableExp = false;
    protected boolean allowSpectators = true;

    private BattleBuilder() {
    }

    public BattleBuilder teamOne(BattleParticipant... teamOne) {
        this.teamOne = teamOne;
        return this;
    }

    public BattleBuilder teamTwo(BattleParticipant... teamTwo) {
        this.teamTwo = teamTwo;
        return this;
    }

    public BattleBuilder rules(BattleRules rules) {
        this.rules = rules;
        return this;
    }

    public BattleBuilder endHandler(Consumer<BattleEndEvent> endConsumer) {
        this.endConsumer = endConsumer;
        return this;
    }

    public BattleBuilder startHandler(Consumer<BattleStartedEvent> startConsumer) {
        this.startConsumer = startConsumer;
        return this;
    }

    public BattleBuilder disableExp() {
        return this.expEnabled(false);
    }

    public BattleBuilder expEnabled(boolean expEnabled) {
        this.disableExp = !expEnabled;
        return this;
    }

    public BattleBuilder allowSpectators() {
        return this.allowSpectators(true);
    }

    public BattleBuilder disallowSpectators() {
        return this.allowSpectators(false);
    }

    public BattleBuilder allowSpectators(boolean allowSpectators) {
        this.allowSpectators = allowSpectators;
        return this;
    }

    public BattleController start() {
        BattleController controller = BattleRegistry.startBattle(this.teamOne, this.teamTwo, this.rules);
        BattleBuilderFactory.registerBattleBuilder(controller, this);
        return controller;
    }

    public static BattleBuilder builder() {
        return new BattleBuilder();
    }
}
