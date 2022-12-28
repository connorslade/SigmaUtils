package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class Scale extends Module {
    public static NumberSetting scale = new NumberSetting(Scale.class, "Scale", 0, 1).description("Scale multiplier")
            .value(1)
            .build();

    public Scale() {
        super("scale", "Scale", "Sets the scale of gui elements on this screen. [EXPERIMENTAL]", Category.Meta);
    }

    public static float getScale() {
        return Config.getEnabled(Scale.class) ? (float) scale.value() : 1f;
    }

    @Override
    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        Components.addToggleButton(screen, this, x, y, 20, true);
        scale.initRender(screen, () -> Text.of(String.format("Scale: %.1f", scale.value() * 100f)), x + 20 + padding, y, 130 - padding);
    }
}
