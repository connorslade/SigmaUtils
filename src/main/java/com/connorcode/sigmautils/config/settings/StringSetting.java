package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.Optional;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class StringSetting extends Setting<StringSetting> {
    String value = "";
    boolean showName = true;
    Callback callback = Optional::of;

    public StringSetting(Class<? extends Module> module, String name) {
        super(module, name);
    }

    public StringSetting build() {
        String moduleId = Config.getModule(this.module).id;
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("util")
                        .then(ClientCommandManager.literal("config")
                                .then(ClientCommandManager.literal(moduleId)
                                        .then(ClientCommandManager.literal(this.id)
                                                .executes(context -> {
                                                    context.getSource()
                                                            .sendFeedback(Text.of(String.format("%s::%s: %s", moduleId,
                                                                    this.id, this.value)));
                                                    return 1;
                                                })
                                                .then(ClientCommandManager.argument("value", greedyString())
                                                        .executes(context -> {
                                                            this.value = context.getArgument("value", String.class);
                                                            context.getSource()
                                                                    .sendFeedback(Text.of(String.format("%s::%s: %s",
                                                                            moduleId, this.id, this.value)));
                                                            return 1;
                                                        })))))));
        return super.build();
    }

    public StringSetting callback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public StringSetting value(String value) {
        this.value = value;
        return this;
    }

    public StringSetting showName(boolean showName) {
        this.showName = showName;
        return this;
    }

    public String value() {
        return value;
    }

    @Override
    public void serialize(NbtCompound nbt) {
        nbt.putString(this.id, this.value);
    }

    @Override
    public void deserialize(NbtCompound nbt) {
        if (!nbt.contains(this.id)) return;
        this.value = nbt.getString(this.id);
    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        MinecraftClient client = MinecraftClient.getInstance();
        int padding = getPadding();

        TextFieldWidget textField = new TextFieldWidget(client.textRenderer, x,
                y + (showName ? client.textRenderer.fontHeight + padding * 2 : 0), width, 20, Text.empty());
        textField.setChangedListener(value -> {
            Optional<String> text = callback.callback(value);
            text.ifPresent(s -> StringSetting.this.value = s);
        });
        textField.setText(this.value);
        Util.addDrawable(screen, textField);

        return 20 + padding + (showName ? client.textRenderer.fontHeight + padding * 4 : 0);
    }

    @Override
    public void render(RenderData data, int x, int y) {
        if (!showName) return;
        MinecraftClient.getInstance().textRenderer.draw(data.matrices(), String.format("§f%s:", this.name), x,
                y + getPadding(), 0);
    }

    interface Callback {
        Optional<String> callback(String value);
    }
}
