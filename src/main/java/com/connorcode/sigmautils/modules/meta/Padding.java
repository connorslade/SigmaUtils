package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

@ModuleInfo(description = "Sets the padding of Sigma Utils gui elements")
public class Padding extends Module {
    public static NumberSetting padding =
            new NumberSetting(Padding.class, "Padding", 0, 20).description("Padding between gui elements")
                    .value(2)
                    .build();

    Text getSliderTitle() {
        return Text.of(String.format("Padding: %d", padding.intValue()));
    }

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        ScreenAccessor sa = (ScreenAccessor) screen;
        Components.addToggleButton(screen, this, x, y, 20, true);
        Util.addChild(screen,
                new Components.TooltipSlider(x + 20 + padding, y, 130 - padding, 20, getSliderTitle(),
                        MathHelper.clamp(padding / 10f, 0, 1)) {
                    @Override
                    protected Text tooltip() {
                        return Padding.padding.getDescription();
                    }

                    @Override
                    protected void updateMessage() {
                        this.setMessage(getSliderTitle());
                    }

                    @Override
                    protected void applyValue() {
                        Padding.padding.value(this.value * 10);
                        if (enabled) sa.invokeClearAndInit();
                    }
                });
    }
}
