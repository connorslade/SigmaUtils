package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class EnumSetting<K extends Enum<?>> implements Setting {
    private final K[] values;
    private final Class<? extends Module> module;
    private final String name;
    private int index;
    private String id;
    private String category = "General";
    private String description;

    public <T extends Module> EnumSetting(Class<T> module, String name, Class<K> enumClass) {
        this.id = Util.toSnakeCase(name);
        this.module = module;
        this.name = name;
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
                .get()
                .ordinal();
        return this;
    }

    public EnumSetting<K> id(String id) {
        this.id = id;
        return this;
    }

    public EnumSetting<K> category(String category) {
        this.category = category;
        return this;
    }

    public EnumSetting<K> description(String description) {
        this.description = description;
        return this;
    }

    public EnumSetting<K> build() {
        Config.moduleSettings.putIfAbsent((Class<Module>) this.module, new ArrayList<>());
        Config.moduleSettings.get(this.module)
                .add(this);

        String moduleId = Config.getModule(this.module).id;
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
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
        return this;
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
    public String getName() {
        return this.name;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public @Nullable Text getDescription() {
        return this.description == null ? null : Text.of(this.description);
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
    public void initRender(Screen screen, int x, int y, int width, int height) {
        Util.addDrawable(screen, new ButtonWidget(x, y, width, height, Text.of(String.format("%s: %s", this.name, this.values[this.index])), (button) -> {
            this.index = (this.index + (Screen.hasShiftDown() ? this.values.length - 1 : 1)) % this.values.length;
            ((ScreenAccessor) screen).invokeClearAndInit();
        }, (((button, matrices, mouseX, mouseY) -> {
            if (this.description == null) return;
            screen.renderOrderedTooltip(matrices, MinecraftClient.getInstance().textRenderer.wrapLines(getDescription(), 200), mouseX, mouseY);
        }))));
    }

    @Override
    public void render(RenderData data, int x, int y) {

    }
}
