package com.connorcode.sigmautils.modules.misc;

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

public class ForceGameTime extends BasicModule {
    public static long forceTime;

    public ForceGameTime() {
        super("force_game_time", "Force Game Time", "Sets how far away the 3rd person camera is", Category.Misc);
    }

    Text getSliderTitle() {
        return Text.of(String.format("Force Game Time: %d", forceTime));
    }

    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        Components.addToggleButton(screen, this, x, y, 20, true);
        Util.addDrawable(screen,
                new SliderWidget(x + 20 + padding, y, 130 - padding, 20, getSliderTitle(),
                        MathHelper.clamp(forceTime / 24000f, 0, 1)) {
                    @Override
                    protected void updateMessage() {
                        this.setMessage(getSliderTitle());
                    }

                    @Override
                    protected void applyValue() {
                        forceTime = (long) (this.value * 24000);
                    }
                });
    }

    public void init() {
        ClientCommandRegistrationCallback.EVENT.register(
                ((dispatcher, registryAccess) -> Util.moduleConfigCommand(dispatcher, this, "time",
                        Datatypes.Long, context -> {
                            forceTime = getLong(context, "setting");
                            return 0;
                        })));
    }

    public void loadConfig(NbtCompound config) {
        super.loadConfig(config);
        forceTime = config.getLong("forceTime");
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = super.saveConfig();
        nbt.putLong("forceTime", forceTime);
        return nbt;
    }
}
