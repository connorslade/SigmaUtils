package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class HotbarPosition extends Module {
    public static int yPosition;

    public HotbarPosition() {
        super("hotbar_position", "Hotbar Position", "Lets you move the hotbar up", Category.Interface);
    }

    Text getSliderTitle() {
        return Text.of(String.format("Hotbar Position: %d", yPosition));
    }

    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        Components.addToggleButton(screen, this, x, y, 20, true);
        Util.addDrawable(screen,
                new SliderWidget(x + 20 + padding, y, 130 - padding, 20, getSliderTitle(), yPosition / 10f) {
                    @Override
                    protected void updateMessage() {
                        this.setMessage(getSliderTitle());
                    }

                    @Override
                    protected void applyValue() {
                        yPosition = (int) (this.value * 10);
                    }
                });
    }

    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
        yPosition = config.getInt("y_position");
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = Util.saveEnabled(enabled);
        nbt.putInt("y_position", yPosition);
        return nbt;
    }
}
