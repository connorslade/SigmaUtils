package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.mixin.InGameHudAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;

import java.util.*;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class Hud extends Module {
    public static List<HudModule> hudModules = new ArrayList<>();
    public static EnumSetting<RealLocation> location =
            new EnumSetting<>(Hud.class, "Location", RealLocation.class).value(RealLocation.BottomLeft)
                    .description("The location of the HUD stack")
                    .build();

    public Hud() {
        super("hud", "Hud", "The basic text hud that can be placed in the corners of the window", Category.Hud);
    }

    public static HashMap<RealLocation, List<String>> makeHudText() {
        HashMap<RealLocation, List<String>> lines = new HashMap<>();
        hudModules.stream()
                .filter(m -> m.enabled)
                .sorted(Comparator.comparingInt(m -> (int) m.order.value()))
                .forEach(m -> {
                    RealLocation location = RealLocation.fromLocation(m.location.value());
                    lines.putIfAbsent(location, new ArrayList<>());
                    lines.get(location).addAll(m.lines());
                });
        return lines;
    }

    public static List<HudStack> getHud(int scaledHeight, int scaledWidth) {
        MinecraftClient client = MinecraftClient.getInstance();

        HashMap<RealLocation, List<String>> lines = makeHudText();
        List<HudStack> hudStacks = new ArrayList<>();

        for (Map.Entry<RealLocation, List<String>> hudStack : lines.entrySet()) {
            int maxWidth =
                    hudStack.getValue().stream().map(client.textRenderer::getWidth).max(Integer::compareTo).orElse(0);
            Pair<Integer, Integer> pos =
                    hudStack.getKey().getPos(scaledHeight, scaledWidth, maxWidth, hudStack.getValue().size());
            hudStacks.add(
                    new HudStack(hudStack.getKey(), hudStack.getValue(), pos.getLeft(), pos.getRight(), maxWidth));
        }

        return hudStacks;
    }

    public static void renderHud(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        InGameHudAccessor hudAccessor = (InGameHudAccessor) client.inGameHud;
        int padding = getPadding();

        client.getProfiler().push("SigmaUtils::Hud");
        getHud(hudAccessor.getScaledHeight(), hudAccessor.getScaledWidth()).forEach(
                hudStack -> hudStack.render(matrices, padding));
        client.getProfiler().pop();
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

    public enum RealLocation {
        TopLeft,
        TopRight,
        BottomRight,
        BottomLeft;

        static RealLocation fromLocation(HudModule.Location location) {
            return switch (location) {
                case TopLeft -> RealLocation.TopLeft;
                case TopRight -> RealLocation.TopRight;
                case BottomRight -> RealLocation.BottomRight;
                case BottomLeft -> RealLocation.BottomLeft;
                case Auto -> Hud.location.value();
            };
        }

        Pair<Integer, Integer> getPos(int scaledHeight, int scaledWidth, int maxWidth, int lines) {
            int fontHeight = MinecraftClient.getInstance().textRenderer.fontHeight;
            int padding = getPadding();

            int x = switch (this) {
                case BottomLeft, TopLeft -> padding;
                case BottomRight, TopRight -> scaledWidth - maxWidth - padding;
            };
            int y = switch (this) {
                case TopLeft, TopRight -> padding;
                case BottomLeft, BottomRight -> scaledHeight - (fontHeight + padding) * lines;
            };
            return new Pair<>(x, y);
        }

        boolean isRight() {
            return this == TopRight || this == BottomRight;
        }
    }

    record HudStack(RealLocation location, List<String> lines, int startX, int startY, int maxWidth) {
        void render(MatrixStack matrices, int padding) {
            MinecraftClient client = MinecraftClient.getInstance();
            int y = startY;
            for (String line : lines) {
                client.textRenderer.drawWithShadow(matrices, line,
                        location.isRight() ? startX + (maxWidth - client.textRenderer.getWidth(line)) : startX, y, 0);
                y += client.textRenderer.fontHeight + padding;
            }
        }
    }
}
