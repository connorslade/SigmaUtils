package com.connorcode.sigmautils.misc;

import net.minecraft.nbt.NbtCompound;

public class Util {
    public static boolean loadEnabled(NbtCompound config) {
        return config.getBoolean("enabled");
    }

    public static NbtCompound saveEnabled(boolean enabled) {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("enabled", enabled);
        return nbt;
    }
}
