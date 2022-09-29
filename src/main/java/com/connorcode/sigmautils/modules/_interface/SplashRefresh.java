package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.Datatypes;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;
import static com.mojang.brigadier.arguments.LongArgumentType.getLong;

public class SplashRefresh extends BasicModule {
    public static long refreshTime;

    public SplashRefresh() {
        super("splash_refresh", "Splash Refresh", "Randomly cycles through splash texts", Category.Interface);
    }

    Text getSliderTitle() {
        return Text.of(String.format("Splash Refresh: %.2fs", refreshTime / 1000f));
    }

    @Override
    public void init() {
        ClientCommandRegistrationCallback.EVENT.register(
                ((dispatcher, registryAccess) -> Util.moduleConfigCommand(dispatcher, this, "refreshTime",
                        Datatypes.Integer, context -> {
                            refreshTime = getLong(context, "setting");
                            return 0;
                        })));
    }

    @Override
    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        Components.addToggleButton(screen, this, x, y, 20, true);
        Util.addDrawable(screen,
                new SliderWidget(x + 20 + padding, y, 130 - padding, 20, getSliderTitle(),
                        MathHelper.clamp(refreshTime / 10000f, 0, 1)) {
                    @Override
                    protected void updateMessage() {
                        this.setMessage(getSliderTitle());
                    }

                    @Override
                    protected void applyValue() {
                        refreshTime = (int) (this.value * 10000);
                    }
                });
    }

    @Override
    public void loadConfig(NbtCompound config) {
        super.loadConfig(config);
        refreshTime = config.getLong("refreshTime");
        if (!config.contains("refreshTime")) refreshTime = 5000;
    }

    @Override
    public NbtCompound saveConfig() {
        NbtCompound nbt = super.saveConfig();
        nbt.putLong("refreshTime", refreshTime);
        return nbt;
    }
}
