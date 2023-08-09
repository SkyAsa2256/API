package com.envyful.api.reforged.config;

import com.envyful.api.forge.config.ConfigCost;
import com.pixelmonmod.pixelmon.api.economy.BankAccount;
import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class MoneyConfigCost implements ConfigCost {

    private double amount;

    public MoneyConfigCost() {
    }

    public MoneyConfigCost(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean has(ServerPlayerEntity player) {
        BankAccount bankAccount = BankAccountProxy.getBankAccountUnsafe(player);
        return bankAccount != null && bankAccount.getBalance().doubleValue() >= this.amount;
    }

    @Override
    public void take(ServerPlayerEntity player) {
        BankAccount bankAccount = BankAccountProxy.getBankAccountUnsafe(player);
        bankAccount.take(this.amount);
    }
}
