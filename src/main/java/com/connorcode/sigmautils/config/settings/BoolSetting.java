package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.misc.Components;
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

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;


public class BoolSetting extends Setting<BoolSetting> {
    boolean value;
    DisplayType displayType = DisplayType.BUTTON;

    public BoolSetting(Class<? extends Module> module, String name) {
        super(module, name);
    }

    @Override
    protected BoolSetting getThis() {
        return this;
    }

    public BoolSetting build() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> {
                    String moduleId = SigmaUtils.modules.get(this.module).id;
                    dispatcher.register(ClientCommandManager.literal("util")
                            .then(ClientCommandManager.literal("config")
                                    .then(ClientCommandManager.literal(moduleId)
                                            .then(ClientCommandManager.literal(this.id)
                                                    .executes(context -> {
                                                        context.getSource()
                                                                .sendFeedback(
                                                                        Text.of(String.format("%s::%s: %s", moduleId,
                                                                                this.id, this.value)));
                                                        return 1;
                                                    })
                                                    .then(ClientCommandManager.argument("value", bool())
                                                            .executes(context -> {
                                                                this.value =
                                                                        context.getArgument("value", Boolean.class);
                                                                context.getSource()
                                                                        .sendFeedback(
                                                                                Text.of(String.format(
                                                                                        "Set %s::%s to %s",
                                                                                        moduleId, this.id,
                                                                                        this.value)));
                                                                return 1;
                                                            }))))));
                });

        return super.build();
    }

    public BoolSetting value(boolean value) {
        this.value = value;
        return this;
    }

    public BoolSetting displayType(DisplayType displayType) {
        this.displayType = displayType;
        return this;
    }

    public boolean value() {
        return this.value;
    }

    @Override
    public void serialize(NbtCompound nbt) {
        nbt.putBoolean(this.id, this.value);
    }

    @Override
    public void deserialize(NbtCompound nbt) {
        if (!nbt.contains(this.id)) return;
        this.value = nbt.getBoolean(this.id);
    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        switch (this.displayType) {
            case BUTTON -> Util.addDrawable(screen, new ButtonWidget(x, y, width, 20,
                    Text.of(String.format("%s: %s", this.name, BoolSetting.this.value ? "On" : "Off")), (button) -> {
                BoolSetting.this.value ^= true;
                ((ScreenAccessor) screen).invokeClearAndInit();
            }, (((button, matrices, mouseX, mouseY) -> {
                if (this.description == null) return;
                screen.renderOrderedTooltip(matrices,
                        MinecraftClient.getInstance().textRenderer.wrapLines(getDescription(), 200), mouseX, mouseY);
            }))));
            case CHECKBOX -> Util.addDrawable(screen,
                    new Components.EventCheckbox(x, y, width, 20, Text.of(this.name), BoolSetting.this.value,
                            (button -> BoolSetting.this.value = button.isChecked()),
                            ((button, matrices, mouseX, mouseY) -> {
                                if (this.description == null) return;
                                screen.renderOrderedTooltip(matrices,
                                        MinecraftClient.getInstance().textRenderer.wrapLines(getDescription(), 200),
                                        mouseX, mouseY);
                            })));
        }

        return 20;
    }

    @Override
    public void render(RenderData data, int x, int y) {

    }

    public enum DisplayType {
        BUTTON,
        CHECKBOX
    }
}
