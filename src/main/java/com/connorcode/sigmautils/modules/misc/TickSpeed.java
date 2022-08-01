package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.Datatypes;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.mixin.MinecraftClientAccessor;
import com.connorcode.sigmautils.mixin.RenderTickCounterAccessor;
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
import static com.mojang.brigadier.arguments.LongArgumentType.getLong;

public class TickSpeed extends Module {
    long mspt;

    public TickSpeed() {
        super("tick_speed", "Tick Speed", "Sets the clients tick speed in MSPT", Category.Misc);
    }

    Text getSliderTitle() {
        return Text.of(String.format("Tick Speed: %d [%d%%]", mspt, Math.round((100 - mspt) / .5)));
    }

    void setTickSpeed(long mspt) {
        ((RenderTickCounterAccessor) ((MinecraftClientAccessor) MinecraftClient.getInstance()).getRenderTickCounter()).tickTime(
                mspt);
    }

    void setTickSpeedFromPercent(double percent) {
        mspt = (long) (50d * (percent * 2d));
        mspt = mspt < 1 ? 1 : mspt;
        if (enabled) setTickSpeed(mspt);
    }

    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        Components.addToggleButton(screen, this, x, y, 20, true);
        Util.addDrawable(screen,
                new SliderWidget(x + 20 + padding, y, 130 - padding, 20, getSliderTitle(),
                        MathHelper.clamp(mspt / 100d, 0, 1)) {
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

    public void init() {
        ClientCommandRegistrationCallback.EVENT.register(
                ((dispatcher, registryAccess) -> Util.moduleConfigCommand(dispatcher, this, "mspt",
                        Datatypes.Long, context -> {
                            mspt = getLong(context, "setting");
                            return 0;
                        })));
    }

    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
        mspt = config.contains("mspt") ? config.getLong("mspt") : 50;
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = Util.saveEnabled(enabled);
        nbt.putLong("mspt", mspt);
        return nbt;
    }

    public void enable(MinecraftClient client) {
        setTickSpeed(mspt);
    }

    public void disable(MinecraftClient client) {
        setTickSpeed(50);
    }
}
