package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;

public class NumberSetting implements Setting {
    private final Class<? extends Module> module;
    private final String name;
    private final double min;
    private final double max;
    private boolean enforceBounds;
    private double value;
    private String id;
    private String category = "General";
    private String description;
    // Number of decimal places to display
    private int precision = 2;

    public <T extends Module> NumberSetting(Class<T> module, String name, double min, double max) {
        this.module = module;
        this.name = name;
        this.min = min;
        this.max = max;
        this.id = Util.toSnakeCase(name);
    }

    public NumberSetting build() {
        if (this.min > this.max) throw new RuntimeException("Min cannot be greater than max");
        Config.moduleSettings.putIfAbsent((Class<Module>) this.module, new ArrayList<>());
        Config.moduleSettings.get(this.module)
                .add(this);

        String moduleId = Config.getModule(this.module).id;
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(ClientCommandManager.literal("util")
                    .then(ClientCommandManager.literal("config")
                            .then(ClientCommandManager.literal(moduleId)
                                    .then(ClientCommandManager.literal(this.id)
                                            .executes(context -> {
                                                context.getSource()
                                                        .sendFeedback(Text.of(String.format("%s::%s: %." + this.precision + "f", moduleId, this.id, this.value)));
                                                return 1;
                                            })
                                            .then(ClientCommandManager.argument("value", doubleArg())
                                                    .executes(context -> {
                                                        double value = context.getArgument("value", Double.class);
                                                        if (enforceBounds && (value < this.min || value > this.max)) {
                                                            context.getSource()
                                                                    .sendError(Text.of(String.format("Value must be between %s and %s", this.min, this.max)));
                                                            return 0;
                                                        }
                                                        this.value = value;
                                                        context.getSource()
                                                                .sendFeedback(Text.of(String.format("Set %s::%s to %." + this.precision + "f", moduleId, this.id, this.value)));
                                                        return 1;
                                                    }))))));
        });

        return this;
    }

    public NumberSetting category(String category) {
        this.category = category;
        return this;
    }

    public NumberSetting value(double value) {
        this.value = value;
        return this;
    }

    public NumberSetting description(String description) {
        this.description = description;
        return this;
    }

    public NumberSetting id(String id) {
        this.id = id;
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

    public double value() {
        return value;
    }

    public int intValue() {
        return (int) value;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Nullable
    @Override
    public Text getDescription() {
        return this.description == null ? null : Text.of(this.description);
    }

    private Text sliderTitle() {
        return Text.of(String.format("%s: %." + this.precision + "f", this.name, this.value));
    }

    @Override
    public void initRender(Screen screen, int x, int y) {
        int padding = getPadding();

        Util.addDrawable(screen, new Components.TooltipSlider(x + 20 + padding, y, 130 - padding, 20, sliderTitle(), (this.value - this.min) / this.max) {
            @Override
            protected Text getTooltip() {
                return Text.of(NumberSetting.this.description);
            }

            @Override
            protected void updateMessage() {
                this.setMessage(sliderTitle());
            }

            @Override
            protected void applyValue() {
                NumberSetting.this.value = this.value * (max - min) + min;
            }
        });
    }

    @Override
    public void render(RenderData data, int x, int y) {

    }
}
