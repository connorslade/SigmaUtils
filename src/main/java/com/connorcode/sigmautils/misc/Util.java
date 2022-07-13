package com.connorcode.sigmautils.misc;

import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class Util {
    public static boolean loadEnabled(NbtCompound config) {
        return config.getBoolean("enabled");
    }

    public static NbtCompound saveEnabled(boolean enabled) {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("enabled", enabled);
        return nbt;
    }

    public static void addMiniToggleButton(Screen screen, Module module, int x, int y) {
        ScreenAccessor sa = (ScreenAccessor) screen;
        addDrawable(screen,
                new ButtonWidget(x, y, 20, 20, Text.of(String.format("§%s█", module.enabled ? "a" : "c")), button -> {
                    module.enabled ^= true;
                    if (module.enabled) module.enable(MinecraftClient.getInstance());
                    else module.disable(MinecraftClient.getInstance());
                    sa.invokeClearAndInit();
                }, (button, matrices, mouseX, mouseY) -> screen.renderOrderedTooltip(matrices,
                        sa.getTextRenderer()
                                .wrapLines(Text.of(module.description), 200), mouseX, mouseY)));
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
