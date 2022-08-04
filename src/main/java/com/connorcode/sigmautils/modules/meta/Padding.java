package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.Datatypes;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;

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
        Components.addToggleButton(screen, this, x, y, 20, true);
        Util.addDrawable(screen,
                new SliderWidget(x + 20 + padding, y, 130 - padding, 20, getSliderTitle(),
                        MathHelper.clamp(padding / 10f, 0, 1)) {
                    @Override
                    protected void updateMessage() {
                        this.setMessage(getSliderTitle());
                    }

                    @Override
                    protected void applyValue() {
                        PADDING = (int) (this.value * 10);
                        if (enabled) sa.invokeClearAndInit();
                    }
                });
    }

    public void init() {
        ClientCommandRegistrationCallback.EVENT.register(
                ((dispatcher, registryAccess) -> Util.moduleConfigCommand(dispatcher, this, "time",
                        Datatypes.Integer, context -> {
                            PADDING = getInteger(context, "setting");
                            return 0;
                        })));
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
