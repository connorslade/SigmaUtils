package com.connorcode.sigmautils.misc;

import com.connorcode.sigmautils.mixin.ScreenAccessor;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
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

    public static void addDrawable(Screen screen, Drawable drawable) {
        ScreenAccessor sa = ((ScreenAccessor) screen);
        sa.getDrawables()
                .add(drawable);
        sa.getChildren()
                .add(drawable);
        sa.getSelectables()
                .add(drawable);
    }
}
