package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class EnumSetting<K extends Enum<?>> extends Setting<EnumSetting<K>> {
    private final K[] values;
    private int index;

    public <T extends Module> EnumSetting(Class<T> module, String name, Class<K> enumClass) {
        super(module, name);
        try {
            this.values = (K[]) enumClass.getMethod("values")
                    .invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public EnumSetting<K> value(K value) {
        this.index = Arrays.stream(values)
                .filter(v -> v == value)
                .findFirst()
                .orElseThrow()
                .ordinal();
        return this;
    }

    @Override
    protected EnumSetting<K> getThis() {
        return this;
    }

    public EnumSetting<K> build() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            String moduleId = SigmaUtils.modules.get(this.module).id;
            dispatcher.register(ClientCommandManager.literal("util")
                    .then(ClientCommandManager.literal("config")
                            .then(ClientCommandManager.literal(moduleId)
                                    .then(ClientCommandManager.literal(this.id)
                                            .executes(context -> {
                                                context.getSource()
                                                        .sendFeedback(Text.of("Current value: " + this.value()));
                                                return 1;
                                            })
                                            .then(ClientCommandManager.argument("value", greedyString())
                                                    .suggests((context, builder) -> {
                                                        for (K value : this.values)
                                                            builder.suggest(value.name());
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(context -> {
                                                        String value = context.getArgument("value", String.class);
                                                        for (int i = 0; i < values.length; i++) {
                                                            if (this.values[i].name()
                                                                    .equalsIgnoreCase(value)) {
                                                                this.index = i;
                                                                return 1;
                                                            }
                                                        }
                                                        context.getSource()
                                                                .sendError(Text.of("Invalid value"));
                                                        return 0;
                                                    }))))));
        }));

        return super.build();
    }

    public int index() {
        return this.index;
    }

    public K value() {
        return this.values[this.index];
    }

    public K[] values() {
        return this.values;
    }

    @Override
    public void serialize(NbtCompound nbt) {
        nbt.putString(this.id, this.value()
                .name());
    }

    @Override
    public void deserialize(NbtCompound nbt) {
        if (!nbt.contains(this.id)) return;
        Arrays.stream(values)
                .filter(v -> v.name()
                        .equalsIgnoreCase(nbt.getString(this.id)))
                .findFirst()
                .ifPresent(k -> this.index = k
                        .ordinal());
    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        Util.addChild(screen,
                new ButtonWidget(x, y, width, 20, Text.of(String.format("%s: %s", this.name, this.values[this.index])),
                        (button) -> {
                            this.index = (this.index + (Screen.hasShiftDown() ? this.values.length - 1 : 1)) %
                                    this.values.length;
                            ((ScreenAccessor) screen).invokeClearAndInit();
                        }, (((button, matrices, mouseX, mouseY) -> {
                    if (this.description == null) return;
                    screen.renderOrderedTooltip(matrices,
                            MinecraftClient.getInstance().textRenderer.wrapLines(getDescription(), 200), mouseX,
                            mouseY);
                }))));

        return 20;
    }

    @Override
    public void render(RenderData data, int x, int y) {

    }
}
