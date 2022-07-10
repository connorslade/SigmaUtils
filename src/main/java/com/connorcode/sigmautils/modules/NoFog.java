package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.Category;
import com.connorcode.sigmautils.Module;
import net.minecraft.nbt.NbtCompound;

public class NoFog extends Module {
    public NoFog() {
        super("no_fog", "No Fog", "Removes fog rendering", Category.World);
    }

    public void loadConfig(NbtCompound config) {
        enabled = config.getBoolean("enabled");
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("enabled", enabled);
        return nbt;
    }
}
