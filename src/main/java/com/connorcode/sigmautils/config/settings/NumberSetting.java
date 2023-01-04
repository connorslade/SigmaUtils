package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;

public class NumberSetting extends Setting<NumberSetting> {
    private final double min;
    private final double max;
    private boolean enforceBounds;
    private double value;
    // Number of decimal places to display
    private int precision = 2;

    public <T extends Module> NumberSetting(Class<T> module, String name, double min, double max) {
        super(module, name);
        this.name = name;
        this.min = min;
        this.max = max;
        this.id = Util.toSnakeCase(name);
    }

    public NumberSetting build() {
        if (this.min > this.max) throw new RuntimeException("Min cannot be greater than max");

        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, dedicated) -> {
                    String moduleId = SigmaUtils.modules.get(this.module).id;
                    dispatcher.register(ClientCommandManager.literal("util")
                            .then(ClientCommandManager.literal("config")
                                    .then(ClientCommandManager.literal(moduleId)
                                            .then(ClientCommandManager.literal(this.id)
                                                    .executes(context -> {
                                                        context.getSource()
                                                                .sendFeedback(Text.of(String.format(
                                                                        "%s::%s: %." + this.precision + "f", moduleId,
                                                                        this.id, this.value)));
                                                        return 1;
                                                    })
                                                    .then(ClientCommandManager.argument("value", doubleArg())
                                                            .executes(context -> {
                                                                double value =
                                                                        context.getArgument("value", Double.class);
                                                                if (enforceBounds &&
                                                                        (value < this.min || value > this.max)) {
                                                                    context.getSource()
                                                                            .sendError(Text.of(String.format(
                                                                                    "Value must be between %s and %s",
                                                                                    this.min, this.max)));
                                                                    return 0;
                                                                }
                                                                this.value = value;
                                                                context.getSource()
                                                                        .sendFeedback(Text.of(String.format(
                                                                                "Set %s::%s to %." + this.precision +
                                                                                        "f",
                                                                                moduleId, this.id, this.value)));
                                                                return 1;
                                                            }))))));
                });

        return super.build();
    }

    public NumberSetting value(double value) {
        this.value = value;
        return this;
    }

    public NumberSetting precision(int precision) {
        this.precision = precision;
        return this;
    }

    public NumberSetting enforceBounds(boolean enforceBounds) {
        this.enforceBounds = enforceBounds;
        return this;
    }

    public int getPrecision() {
        return this.precision;
    }

    public double value() {
        return value;
    }

    public int intValue() {
        return (int) value;
    }

    @Nullable
    @Override
    public Text getDescription() {
        return this.description == null ? null : Text.of(this.description);
    }

    @Override
    public void serialize(NbtCompound nbt) {
        if (this.precision == 0) nbt.putInt(this.id, this.intValue());
        else nbt.putDouble(this.id, this.value);
    }

    @Override
    public void deserialize(NbtCompound nbt) {
        if (!nbt.contains(this.id)) return;
        if (this.precision == 0) this.value = nbt.getInt(this.id);
        else this.value = nbt.getDouble(this.id);
    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        return initRender(screen, () -> Text.of(String.format("%s: %." + this.precision + "f", this.name, this.value)),
                x, y, width);
    }

    public int initRender(Screen screen, SliderText slider, int x, int y, int width) {
        Util.addDrawable(screen, new Components.TooltipSlider(x, y, width, 20, slider.getText(),
                MathHelper.clamp((this.value - this.min) / this.max, 0, 1)) {
            @Override
            protected Text getTooltip() {
                return Text.of(NumberSetting.this.description);
            }

            @Override
            protected void updateMessage() {
                this.setMessage(slider.getText());
            }

            @Override
            protected void applyValue() {
                NumberSetting.this.value = this.value * (max - min) + min;
            }
        });

        return 20;
    }

    @Override
    public void render(RenderData data, int x, int y) {

    }

    public interface SliderText {
        Text getText();
    }
}
