package com.envyful.api.reforged.pixelmon.config;

import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class LoreConfig {

    public static final transient LoreConfig DEFAULT = new LoreConfig();

    private List<String> lore = Lists.newArrayList(
            "&7Nature: &b%nature%",
            "&7Ability: &b%ability%",
            "&7Friendship: &b%friendship%",
            "&7Untradeable: &b%untradeable%",
            " ",
            "&7IVs (&b%iv_percentage%&7):",
            "    §7HP: &b%iv_hp% §d| §7Atk: &b%iv_attack% §d| §7Def: &b%iv_defence%",
            "    §7SAtk: &b%iv_spattack% §d| §7SDef: &b%iv_spdefence% §d| §7Spd: &b%iv_speed%",
            " ",
            "&7EVs:",
            "    §7HP: &b%ev_hp% §d| §7Atk: &b%ev_attack% §d| §7Def: &b%ev_defence%",
            "    §7SAtk: &b%ev_spattack% §d| §7SDef: &b%ev_spdefence% §d| §7Spd: &b%ev_speed%",
            " ",
            "&7Moves:",
            "    &b%move_1%",
            "    &b%move_2%",
            "    &b%move_3%",
            "    &b%move_4%"
    );

    private String untrdeableTrueFormat = "&aTRUE";
    private String untradeableFalseFormat = "&cFALSE";

    public LoreConfig() {}

    public List<String> getLore() {
        return this.lore;
    }

    public String getUntrdeableTrueFormat() {
        return this.untrdeableTrueFormat;
    }

    public String getUntradeableFalseFormat() {
        return this.untradeableFalseFormat;
    }
}
