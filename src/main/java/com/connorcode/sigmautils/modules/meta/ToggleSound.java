package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Datatypes;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;

import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;

public class ToggleSound extends Module {
    public static float enablePitch;
    public static float disablePitch;

    public ToggleSound() {
        super("toggle_sound", "Toggle Sound", "Plays a click sound when a module is en/disabled.", Category.Meta);
    }

    public static void play(boolean enable) {
        if (!Config.getEnabled(ToggleSound.class)) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Play button click sound
        client.getSoundManager()
                .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, enable ? enablePitch : disablePitch));
    }

    @Override
    public void init() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            Util.moduleConfigCommand(dispatcher, this, "enable_pitch", Datatypes.Float, context -> {
                enablePitch = getFloat(context, "setting");
                return 0;
            });

            Util.moduleConfigCommand(dispatcher, this, "disable_pitch", Datatypes.Float, context -> {
                disablePitch = getFloat(context, "setting");
                return 0;
            });
        }));
    }

    @Override
    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
        enablePitch = config.contains("enable_pitch") ? config.getFloat("enable_pitch") : 1f;
        disablePitch = config.contains("disable_pitch") ? config.getFloat("disable_pitch") : 0.7f;
    }

    @Override
    public NbtCompound saveConfig() {
        NbtCompound nbt = Util.saveEnabled(enabled);
        nbt.putFloat("enable_pitch", enablePitch);
        nbt.putFloat("disable_pitch", disablePitch);
        return nbt;
    }
}
