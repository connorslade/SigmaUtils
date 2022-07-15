package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class CameraDistance extends Module {
    public static double distance;

    public CameraDistance() {
        super("camera_distance", "CameraDistance", "Sets how far away the 3rd person camera is", Category.Misc);
    }

    Text getSliderTitle() {
        return Text.of(String.format("Camera Distance: %.1f", distance));
    }

    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        Util.addMiniToggleButton(screen, this, x, y);
        Util.addDrawable(screen,
                new SliderWidget(x + 20 + padding, y, 130 - padding, 20, getSliderTitle(), distance / 50) {
                    @Override
                    protected void updateMessage() {
                        this.setMessage(getSliderTitle());
                    }

                    @Override
                    protected void applyValue() {
                        distance = this.value * 50;
                    }
                });
    }

    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
        distance = config.contains("distance") ? config.getDouble("distance") : 4;
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = Util.saveEnabled(enabled);
        nbt.putDouble("distance", distance);
        return nbt;
    }
}
