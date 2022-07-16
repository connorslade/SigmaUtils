package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.*;

public class EffectHud extends HudModule {
    public EffectHud() {
        super("effect_hud", "Effect Hud", "Shows all the status effects you have the HUD", Category.Hud);
    }

    public List<String> lines() {
        List<String> out = new ArrayList<>();
        Map<StatusEffect, StatusEffectInstance> effectMap = Objects.requireNonNull(MinecraftClient.getInstance().player)
                .getActiveStatusEffects();

        for (Map.Entry<StatusEffect, StatusEffectInstance> i : effectMap.entrySet()) {
            Optional<RegistryKey<StatusEffect>> effectKey = Registry.STATUS_EFFECT.getKey(i.getKey());
            if (effectKey.isEmpty()) continue;

            out.add(String.format("§r§bEffect: §f%s %d %ds", effectKey.get()
                    .getValue(), i.getValue()
                    .getAmplifier() + 1, i.getValue()
                    .getDuration() / 20));
        }

        return out;
    }
}
