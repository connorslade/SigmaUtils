package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.HudModule;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Shows all the status effects you have in the HUD")
public class EffectHud extends HudModule {
    public EffectHud() {
        super();
        this.defaultTextColor = TextStyle.Color.Aqua;
        this.defaultOrder = 5;
    }

    public List<String> lines() {
        List<String> out = new ArrayList<>();
        String color = this.getTextColor();
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> effectMap = Objects.requireNonNull(client.player).getActiveStatusEffects();

        for (var i : effectMap.entrySet()) {
            var name = Text.translatable(i.getValue().getTranslationKey()).getString();
            out.add(String.format("§r%sEffect: §f%s %d %ds", color, name, i.getValue().getAmplifier() + 1, i.getValue()
                    .getDuration() / 20));
        }

        return out;
    }
}
