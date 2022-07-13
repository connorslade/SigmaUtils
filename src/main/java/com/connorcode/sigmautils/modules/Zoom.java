package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class Zoom extends Module {
    public static float zoom;

    public Zoom() {
        super("zoom", "Zoom", "Zoom (Fov multiplier)", Category.Rendering);
    }

    Text getSliderTitle() {
        return Text.of(String.format("Zoom: %.1fx", 10 - (zoom * 10)));
    }

    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        Util.addMiniToggleButton(screen, this, x, y);
        Util.addDrawable(screen,
                new SliderWidget(x + 20 + padding, y, 130 - padding, 20, getSliderTitle(), 1 - zoom) {
                    @Override
                    protected void updateMessage() {
                        this.setMessage(getSliderTitle());
                    }

                    @Override
                    protected void applyValue() {
                        zoom = 1f - (float) this.value;
                    }
                });
    }

    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
        zoom = config.contains("zoom") ? config.getFloat("zoom") : .1f;
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = Util.saveEnabled(enabled);
        nbt.putFloat("zoom", zoom);
        return nbt;
    }
}
