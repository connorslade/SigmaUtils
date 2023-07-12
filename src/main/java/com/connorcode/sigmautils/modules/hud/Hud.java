package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.mixin.InGameHudAccessor;
import com.connorcode.sigmautils.module.HudModule;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Pair;

import java.util.*;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

@ModuleInfo(description = "The basic text hud that can be placed in the corners of the window")
public class Hud extends Module {
    public static List<HudModule> hudModules = new ArrayList<>();
    public static EnumSetting<RealLocation> location =
            new EnumSetting<>(Hud.class, "Location", RealLocation.class).value(RealLocation.BottomLeft)
                    .description("The location of the HUD stack")
                    .build();
    public static BoolSetting hideF3 = new BoolSetting(Hud.class, "F3 Hide").value(true)
            .description("Hide the HUD when the F3 menu is open")
            .build();
    static BoolSetting background = new BoolSetting(Hud.class, "Background").description(
            "Weather to render a background behind all hud elements.").build();

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

    public static void renderHud(DrawContext ctx) {
        if (client.options.debugEnabled && hideF3.value()) return;
        InGameHudAccessor hudAccessor = (InGameHudAccessor) client.inGameHud;
        int padding = getPadding();

        client.getProfiler().push("SigmaUtils::Hud");
        getHud(hudAccessor.getScaledHeight(), hudAccessor.getScaledWidth()).forEach(
                hudStack -> hudStack.render(ctx, padding));
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
            int fontHeight = client.textRenderer.fontHeight;
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
        void render(DrawContext ctx, int padding) {
            int y = startY;
            var p = padding / 2f;
            var pr = (int) Math.ceil(p);
            for (String line : lines) {
                var x = location.isRight() ? startX + (maxWidth - client.textRenderer.getWidth(line)) : startX;
                if (background.value()) ctx.fill(x - pr, y - pr, x + client.textRenderer.getWidth(line) + pr,
                        y + client.textRenderer.fontHeight + (int) (p), 0x88000000);
                ctx.drawTextWithShadow(client.textRenderer, line, x, y, 0);
                y += client.textRenderer.fontHeight + padding;
            }
        }
    }
}
