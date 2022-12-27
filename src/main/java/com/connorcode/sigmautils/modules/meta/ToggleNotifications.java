package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class ToggleNotifications extends Module {
    // True => Chat
    // False => Action Bar
    public static boolean display;

    public ToggleNotifications() {
        super("toggle_notifications", "Toggle Notifications", "Shows a message in (C)hat or (A)ction bar",
                Category.Meta);
    }

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        ScreenAccessor sa = (ScreenAccessor) screen;
        int padding = getPadding();

        Components.addToggleButton(screen, this, x, y, 130 - padding, false);
        Util.addDrawable(screen, new ButtonWidget(x + 130, y, 20, 20, Text.of(display ? "C" : "A"), button -> {
            display ^= true;
            sa.invokeClearAndInit();
        }));
    }

    public void loadConfig(NbtCompound config) {
        super.loadConfig(config);
        display = config.getBoolean("display");
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = super.saveConfig();
        nbt.putBoolean("display", display);
        return nbt;
    }
}
