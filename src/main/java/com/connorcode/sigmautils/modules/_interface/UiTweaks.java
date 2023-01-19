package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.nbt.NbtCompound;

public class UiTweaks extends Module {
    public static boolean muted = false;
    public static BoolSetting audioMuteButton = new BoolSetting(UiTweaks.class, "Audio Mute Button")
            .description("Adds a button to pause menu to mute / unmute all audio")
            .displayType(BoolSetting.DisplayType.CHECKBOX)
            .value(true)
            .build();

    public UiTweaks() {
        super("ui_tweaks", "UI Tweaks", "Random interface tweaks.",
                Category.Interface);
    }

    public static boolean isMuted() {
        return Config.getEnabled(UiTweaks.class) && audioMuteButton.value() && muted;
    }

    @Override
    public void loadConfig(NbtCompound config) {
        super.loadConfig(config);
        muted = config.getBoolean("muted");
    }

    @Override
    public NbtCompound saveConfig() {
        var nbt = super.saveConfig();
        nbt.putBoolean("muted", muted);
        return nbt;
    }
}
