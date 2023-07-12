package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.module.ModuleInfo;
import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.nbt.NbtCompound;

@ModuleInfo(description = "Random interface tweaks.")
public class UiTweaks extends Module {
    public static boolean muted = false;
    public static BoolSetting audioMuteButton = new BoolSetting(UiTweaks.class, "Audio Mute Button")
            .description("Adds a button to pause menu to mute / unmute all audio")
            .value(true)
            .build();
    static BoolSetting validSession = new BoolSetting(UiTweaks.class, "Valid Session")
            .description("Adds a display to the multiplayer screen to show if your session is valid")
            .value(true)
            .build();

    static BoolSetting noRealms =
            new BoolSetting(UiTweaks.class, "No Realms").description("Hides the 'Realms' button on the title screen.")
                    .build();

    static BoolSetting fastDoll = new BoolSetting(UiTweaks.class, "Fast Doll")
            .description("Makes the inventory player model render at full speed")
            .value(true)
            .build();

    public static boolean isMuted() {
        return Config.getEnabled(UiTweaks.class) && audioMuteButton.value() && muted;
    }

    public static boolean showValidSession() {
        return Config.getEnabled(UiTweaks.class) && validSession.value();
    }

    public static boolean noRealms() {
        return Config.getEnabled(UiTweaks.class) && noRealms.value();
    }

    public static boolean fastDoll() {
        return Config.getEnabled(UiTweaks.class) && fastDoll.value();
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
