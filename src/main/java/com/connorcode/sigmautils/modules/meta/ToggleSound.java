package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class ToggleSound extends Module {
    public static NumberSetting enablePitch = new NumberSetting(ToggleSound.class, "enablePitch", 0f, 2f).value(1)
            .description("The pitch of the sound to play when a module is enabled")
            .build();
    public static NumberSetting disablePitch = new NumberSetting(ToggleSound.class, "disablePitch", 0f, 2f).value(0.7)
            .description("The pitch of the sound to play when a module is disabled")
            .build();

    public ToggleSound() {
        super("toggle_sound", "Toggle Sound", "Plays a click sound when a module is en/disabled.", Category.Meta);
    }

    public static void play(boolean enable) {
        if (!Config.getEnabled(ToggleSound.class)) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Play button click sound
        client.getSoundManager()
                .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, (float) (enable ? enablePitch.value() : disablePitch.value())));
    }
}
