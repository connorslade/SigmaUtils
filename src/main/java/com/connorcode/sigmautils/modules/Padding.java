package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class Padding extends Module {
    public static int PADDING;

    public Padding() {
        super("padding", "Padding", "Sets the padding of Sigma Utils gui elements", Category.Meta);
    }

    Text getSliderTitle() {
        return Text.of(String.format("Padding: %d", PADDING));
    }

    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        ScreenAccessor sa = (ScreenAccessor) screen;
        Util.addMiniToggleButton(screen, this, x, y);
        sa.invokeAddDrawableChild(
                new SliderWidget(x + 20 + padding, y, 130 - padding, 20, getSliderTitle(), padding / 10f) {
                    @Override
                    protected void updateMessage() {
                        this.setMessage(getSliderTitle());
                    }

                    @Override
                    protected void applyValue() {
                        PADDING = (int) (this.value * 10);
                        sa.invokeClearAndInit();
                    }
                });
    }

    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
        PADDING = config.contains("padding") ? config.getInt("padding") : 2;
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = Util.saveEnabled(enabled);
        nbt.putInt("padding", PADDING);
        return nbt;
    }
}
