package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.*;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class EffectHud extends HudModule {
    public EffectHud() {
        super("effect_hud", "Effect Hud", "Shows all the status effects you have the HUD", Category.Hud);
        this.defaultTextColor = TextStyle.Color.Aqua;
        this.defaultOrder = 5;
    }

    public List<String> lines() {
        List<String> out = new ArrayList<>();
        String color = this.getTextColor();
        Map<StatusEffect, StatusEffectInstance> effectMap = Objects.requireNonNull(client.player)
                .getActiveStatusEffects();

        for (Map.Entry<StatusEffect, StatusEffectInstance> i : effectMap.entrySet()) {
            Optional<RegistryKey<StatusEffect>> effectKey = Registry.STATUS_EFFECT.getKey(i.getKey());
            if (effectKey.isEmpty()) continue;

            out.add(String.format("§r%sEffect: §f%s %d %ds", color, effectKey.get()
                    .getValue(), i.getValue()
                    .getAmplifier() + 1, i.getValue()
                    .getDuration() / 20));
        }

        return out;
    }
}
