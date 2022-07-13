package com.connorcode.sigmautils.modules;

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

public class CoordinatesHud extends Module {
    char[] arrows = new char[]{
            '⬉',
            '⬈',
            '⬊',
            '⬋'
    };
    public static int location;

    public CoordinatesHud() {
        super("coordinates_hud", "Coordinates Hud", "Adds a display on your screen showing your current location",
                Category.Interface);
    }

    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        ScreenAccessor sa = (ScreenAccessor) screen;
        int padding = getPadding();

        Util.addDrawable(screen, new ButtonWidget(x, y, 130 - padding, 20,
                Text.of(String.format("%s█§r %s", enabled ? "§a" : "§c", name)), button -> {
            enabled ^= true;
            if (enabled) enable(client);
            else disable(client);
            sa.invokeClearAndInit();
        }, ((button, matrices, mouseX, mouseY) -> screen.renderOrderedTooltip(matrices, sa.getTextRenderer()
                .wrapLines(Text.of(description), 200), mouseX, mouseY))));
        Util.addDrawable(screen,
                new ButtonWidget(x + 130, y, 20, 20, Text.of(String.valueOf(arrows[location])), button -> {
                    location = (location + 1) % 4;
                    sa.invokeClearAndInit();
                }));
    }

    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
        location = config.contains("location") ? config.getInt("location") : 0;
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = Util.saveEnabled(enabled);
        nbt.putInt("location", location);
        return nbt;
    }
}
