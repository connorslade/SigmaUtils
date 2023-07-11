package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.ModuleInfo;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.config.settings.SelectorSetting;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Plays a click sound when a module is en/disabled.")
public class ToggleSound extends Module {
    private static final SelectorSetting sound =
            new SelectorSetting(ToggleSound.class, "Sound", () -> Registries.SOUND_EVENT.stream()
                    .map(s -> s.getId()
                            .toString())
                    .toList()).value("minecraft:ui.button.click")
                    .description("The sound to play")
                    .build();
    private static final NumberSetting enablePitch =
            new NumberSetting(ToggleSound.class, "enablePitch", 0f, 2f).value(1)
                    .description("The pitch of the sound to play when a module is enabled")
                    .build();
    private static final NumberSetting disablePitch =
            new NumberSetting(ToggleSound.class, "disablePitch", 0f, 2f).value(0.7)
                    .description("The pitch of the sound to play when a module is disabled")
                    .build();

    public static void play(boolean enable) {
        if (!Config.getEnabled(ToggleSound.class)) return;
        if (client.player == null) return;

        // Play button click sound
        SoundEvent soundEvent =
                sound.value() == null ? SoundEvents.UI_BUTTON_CLICK.value() : Registries.SOUND_EVENT.get(
                        Identifier.tryParse(sound.value()));
        client.getSoundManager()
                .play(PositionedSoundInstance.master(Objects.requireNonNull(soundEvent),
                        (float) (enable ? enablePitch.value() : disablePitch.value())));
    }
}
