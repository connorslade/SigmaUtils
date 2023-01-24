package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.MinecraftClientAccessor;
import com.connorcode.sigmautils.mixin.RenderTickCounterAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class TickSpeed extends Module {
    public static NumberSetting mspt = new NumberSetting(TickSpeed.class, "MSPT", 1, 100).description(
                    "Sets the MSPT (milliseconds per tick) of the client side game loop")
            .value(50)
            .build();

    public TickSpeed() {
        super("tick_speed", "Tick Speed", "Sets the clients tick speed in MSPT", Category.Misc);
    }

    Text getSliderTitle() {
        return Text.of(String.format("Tick Speed: %d [%d%%]", mspt.intValue(), Math.round((100 - mspt.value()) / .5)));
    }

    void setTickSpeed(long mspt) {
        ((RenderTickCounterAccessor) ((MinecraftClientAccessor) client).getRenderTickCounter()).tickTime(mspt);
    }

    void setTickSpeedFromPercent(double percent) {
        mspt.value(50d * (percent * 2d));
        mspt.value(Math.max(mspt.intValue(), 1));
        if (enabled) setTickSpeed(mspt.intValue());
    }

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.addToggleButton(screen, this, x, y, 20, true);
        int padding = getPadding();

        Util.addChild(screen, new Components.TooltipSlider(x + 20 + padding, y, 130 - padding, 20, getSliderTitle(),
                MathHelper.clamp(mspt.value() / 100d, 0, 1)) {
            @Override
            protected Text getTooltip() {
                return mspt.getDescription();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(getSliderTitle());
            }

            @Override
            protected void applyValue() {
                setTickSpeedFromPercent(this.value);
            }
        });
    }

    public void enable(MinecraftClient client) {
        setTickSpeed(mspt.intValue());
    }

    public void disable(MinecraftClient client) {
        setTickSpeed(50);
    }
}
