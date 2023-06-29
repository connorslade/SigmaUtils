package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event._interface.MouseScrollEvent;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class Zoom extends Module {
    public static final BoolSetting scrollZoom = new BoolSetting(Zoom.class, "Scroll Zoom")
            .description("Makes the scroll wheal modify the zoom. (when in zoom mode)").build();
    private static final NumberSetting zoom =
            new NumberSetting(Zoom.class, "Zoom", 0, 9.9).description("FOV Multiplier").value(5).build();
    private static final BoolSetting smoothZoom = new BoolSetting(Zoom.class, "Smooth Zoom").value(true)
            .description("Smooth Zoom")
            .category("Smooth Zoom")
            .build();
    private static final NumberSetting tweenTicks = new NumberSetting(Zoom.class, "Tween Ticks", 1, 20).value(5)
            .description("How many ticks it takes to zoom in/out")
            .category("Smooth Zoom")
            .build();
    static double zoomModifier = 1;
    private static long tick = 0;
    private static long animationStart = 0;
//    private final BoolSetting toggle = new BoolSetting(Zoom.class, "Toggle").value(true)
//            .description("Makes this module toggle on and off rather than when the keybinding is held down.")
//            .build();


    public Zoom() {
        super("zoom", "Zoom", "Zoom (Fov multiplier)", Category.Rendering);
    }

    public static double getFov(double fov, float tickDelta) {
        boolean enabled = Config.getEnabled(Zoom.class);
        if (!enabled && !smoothZoom.value()) return fov;
        double zoomTween =
                MathHelper.clamp(smoothZoom.value() ? (tick - animationStart + tickDelta) / tweenTicks.value() : 1, 0,
                        1);
        return fov * ((10 - zoom.value() * (enabled ? zoomTween : 1 - zoomTween)) / 10) * (enabled ? zoomModifier : 1);
    }

    @EventHandler
    void onMouseScroll(MouseScrollEvent event) {
        if (client.currentScreen != null || !enabled || !scrollZoom.value()) return;
        zoomModifier -= event.vertical / 10f;
        event.cancel();
    }

    public void tick() {
        super.tick();

        tick++;
        if (tick <= 0) {
            tick = 0;
            animationStart = 0;
        }
//
//        System.out.printf("%s, %s, %s, %s\n", !enabled, this.toggle.value(),
//                !(Config.moduleSettings.get(this.getClass()).get(1) instanceof KeyBindSetting),
//                ((KeyBindSetting) Config.moduleSettings.get(this.getClass()).get(1)).pressed());
//        if (!enabled || this.toggle.value() ||
//                !(Config.moduleSettings.get(this.getClass()).get(1) instanceof KeyBindSetting keybinding) ||
//                keybinding.pressed()) return;
//        enabled = false;
    }

    @Override
    public void enable(MinecraftClient client) {
        super.enable(client);
        animationStart = tick;
    }

    @Override
    public void disable(MinecraftClient client) {
        super.disable(client);
        animationStart = tick;
        zoomModifier = 1;
    }

    @Override
    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        Components.addToggleButton(screen, this, x, y, 20, true);
        zoom.initRender(screen, () -> Text.of(String.format("%s: %.1fx", this.name, zoom.value())), x + 20 + padding, y,
                130 - padding);
    }
}
