package com.envyful.api.reforged.config;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.ConfigCost;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.pixelmonmod.pixelmon.api.economy.BankAccount;
import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;

@ConfigSerializable
public class MoneyConfigCost implements ConfigCost {

    private double amount;
    private String failureMessage;
    private String successMessage;

    public MoneyConfigCost() {
    }

    public MoneyConfigCost(double amount, String failureMessage, String successMessage) {
        this.amount = amount;
        this.failureMessage = failureMessage;
        this.successMessage = successMessage;
    }

    @Override
    public boolean has(ServerPlayerEntity player) {
        BankAccount bankAccount = BankAccountProxy.getBankAccountUnsafe(player);
        return bankAccount != null && bankAccount.getBalance().doubleValue() >= this.amount;
    }

    @Override
    public void take(ServerPlayerEntity player, Placeholder... placeholders) {
        BankAccount bankAccount = BankAccountProxy.getBankAccountUnsafe(player);
        bankAccount.take(this.amount);

        for (String handlePlaceholder : PlaceholderFactory.handlePlaceholders(Collections.singletonList(this.successMessage), placeholders)) {
            player.sendMessage(UtilChatColour.colour(handlePlaceholder), Util.NIL_UUID);
        }
    }

    @Override
    public String getFailureMessage() {
        return this.failureMessage;
    }
}
