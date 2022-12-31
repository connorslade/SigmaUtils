package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Pair;

import java.util.*;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class Hud extends Module {
    static final String[] hudModules = new String[]{
            "watermark_hud",
            "fps_hud",
            "tps_hud",
            "ping_hud",
            "coordinates_hud",
            "effect_hud",
            "biome_hud",
            "server_hud",
            "time_played_hud",
            "time_hud"
    };
    public static EnumSetting<Location> location =
            new EnumSetting<>(Hud.class, "Location", Location.class).value(Location.BottomLeft)
                    .description("The location of the HUD stack")
                    .build();

    public Hud() {
        super("hud", "Hud", "The basic text hud that can be places in the corners of the window", Category.Hud);
    }

    public static List<String> makeHudText() {
        List<String> lines = new ArrayList<>();
        Arrays.stream(hudModules)
                .map(n -> {
                    Optional<Module> module = SigmaUtilsClient.modules.stream()
                            .filter(m -> Objects.equals(m.id, n))
                            .findFirst();
                    return module.isEmpty() ? SigmaUtilsClient.modules.get(0) : module.get();
                })
                .filter(m -> m.enabled)
                .sorted(Comparator.comparingInt(m -> (int) ((HudModule) m).order.value()))
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

        Pair<Integer, Integer> pos = switch (Hud.location.index()) {
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

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();

        Components.addToggleButton(screen, this, x, y, 130 - padding, false);
        Components.enumConfig(screen, x, y, location, new char[]{
                '⬉',
                '⬈',
                '⬊',
                '⬋'
        });
    }

    public enum Location {
        TopLeft,
        TopRight,
        BottomRight,
        BottomLeft,
    }
}
