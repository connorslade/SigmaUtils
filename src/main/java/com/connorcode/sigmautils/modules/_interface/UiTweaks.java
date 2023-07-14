package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.nbt.NbtCompound;

@ModuleInfo(description = "Random interface tweaks.",
        documentation = "Currently has four tweaks: 'Audio Mute Button', 'Valid Session', 'No Realms', and 'Fast Doll'. Audio Mute Button puts a Mute button for the games audio next to the Music & Sounds button in Options. Valid Session put a bit of text on the top right corner of the Multiplayer Server selector that says if your current session is valid or not. No Realms removes the Realms button from the title screen (because who uses Realms). And Fast Doll renders the little inventory player model at full speed, making it look less choppy.")
public class UiTweaks extends Module {
    public static boolean muted = false;
    public static String craftingBookSearch = "";

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

    static BoolSetting persistentCraftingBookSearch = new BoolSetting(UiTweaks.class, "Persistent Crafting Book Search")
            .description("Makes the search bar in the crafting book persistent")
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

    public static boolean persistentCraftingBookSearch() {
        return Config.getEnabled(UiTweaks.class) && persistentCraftingBookSearch.value();
    }

    @Override
    public void loadConfig(NbtCompound config) {
        super.loadConfig(config);
        muted = config.getBoolean("muted");
        craftingBookSearch = config.getString("craftingBookSearch");
    }

    @Override
    public NbtCompound saveConfig() {
        var nbt = super.saveConfig();
        nbt.putBoolean("muted", muted);
        nbt.putString("craftingBookSearch", craftingBookSearch);
        return nbt;
    }
}
