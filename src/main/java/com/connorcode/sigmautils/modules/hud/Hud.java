package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.*;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class Hud extends Module {
    public static int location;
    static String[] hudModules = new String[]{
            "watermark_hud",
            "fps_hud",
            "tps_hud",
            "ping_hud",
            "coordinates_hud",
            "effect_hud",
            "biome_hud",
            "server_hud"
    };
    char[] arrows = new char[]{
            '⬉',
            '⬈',
            '⬊',
            '⬋'
    };

    public Hud() {
        super("hud", "Hud", "The basic text hud that can be places in the corners of the window", Category.Hud);
    }

    public static List<String> makeHudText() {
        List<String> lines = new ArrayList<>();
        Arrays.stream(hudModules)
                .map(n -> {
                    Optional<Module> module = Arrays.stream(SigmaUtilsClient.modules)
                            .filter(m -> Objects.equals(m.id, n))
                            .findFirst();
                    return module.isEmpty() ? SigmaUtilsClient.modules[0] : module.get();
                })
                .filter(m -> m.enabled)
                .forEach(m -> lines.addAll(((HudModule) m).lines()));
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

        Components.addToggleButton(screen, this, x, y, 130 - padding, false);
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
