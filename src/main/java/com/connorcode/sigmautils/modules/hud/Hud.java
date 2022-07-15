package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class Hud extends Module {
    public static int location;
    char[] arrows = new char[]{
            '⬉',
            '⬈',
            '⬊',
            '⬋'
    };

    public Hud() {
        super("hud", "Hud", "The basic text hud that can be places in the corners of the window", Category.Hud);
    }

    public static List<String> makeHudText() throws Exception {
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        List<String> lines = new ArrayList<>();

        if (Config.getEnabled("watermark_hud")) lines.add("§r§l§aSigma Utils");
        if (Config.getEnabled("coordinates_hud")) lines.add(
                String.format("§r§fPos: %d, %d, %d", (int) Math.round(player.getX()), (int) Math.round(player.getY()),
                        (int) Math.round(player.getZ())));

        return lines;
    }

    public static Pair<List<String>, Pair<Integer, Integer>> getHud(int scaledHeight, int scaledWidth) {
        int padding = getPadding();
        MinecraftClient client = MinecraftClient.getInstance();

        List<String> text = new ArrayList<>();
        try {
            text = makeHudText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = 0;
        for (String i : text) size = Math.max(size, client.textRenderer.getWidth(i));

        Pair<Integer, Integer> pos = switch (Hud.location) {
            case 3:
                yield new Pair<>(padding, scaledHeight - (client.textRenderer.fontHeight + padding) * text.size());
            case 2:
                yield new Pair<>(scaledWidth - size - padding,
                        scaledHeight - (client.textRenderer.fontHeight + padding) * text.size());
            case 1:
                yield new Pair<>(scaledWidth - size - padding, padding);
            case 0:
                yield new Pair<>(padding, padding);
            default:
                throw new IllegalStateException("Unexpected value: " + Hud.location);
        };

        return new Pair<>(text, pos);
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
