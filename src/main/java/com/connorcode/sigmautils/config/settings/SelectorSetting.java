package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

// TODO: Use a ? implementing a option interface to make this more clean
// Also add a search bar because large lists are hard to navigate
// Oh and also also scroll to the selected option
public class SelectorSetting extends Setting<SelectorSetting> {
    String value;
    OptionGetter optionGetter;

    public SelectorSetting(Class<? extends Module> module, String name, OptionGetter optionGetter) {
        super(module, name);
        this.optionGetter = optionGetter;
    }

    public SelectorSetting value(String value) {
        this.value = value;
        return this;
    }

    public String value() {
        return this.value;
    }

    @Override
    protected SelectorSetting getThis() {
        return this;
    }

    public SelectorSetting build() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            String moduleId = SigmaUtils.modules.get(this.module).id;
            dispatcher.register(ClientCommandManager.literal("util")
                    .then(ClientCommandManager.literal("config")
                            .then(ClientCommandManager.literal(moduleId)
                                    .then(ClientCommandManager.literal(this.id)
                                            .executes(context -> {
                                                context.getSource()
                                                        .sendFeedback(
                                                                Text.of(String.format("%s::%s: %s", moduleId, this.id,
                                                                        this.value)));
                                                return 1;
                                            })
                                            .then(ClientCommandManager.argument("value", greedyString())
                                                    .suggests((context, builder) -> {
                                                        for (String option : this.optionGetter.getOptions())
                                                            builder.suggest(option);
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(context -> {
                                                        this.value = context.getArgument("value", String.class);
                                                        context.getSource()
                                                                .sendFeedback(Text.of(String.format("Set %s::%s to %s",
                                                                        moduleId, this.id, this.value)));
                                                        return 1;
                                                    }))))));
        }));

        return super.build();
    }

    @Override
    public void serialize(NbtCompound nbt) {
        if (this.value == null) return;
        nbt.putString(this.id, this.value);
    }

    @Override
    public void deserialize(NbtCompound nbt) {
        if (!nbt.contains(this.id)) return;
        this.value = nbt.getString(this.id);
    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        Util.addChild(screen, ButtonWidget.builder(Text.of(String.format("%s: %s", this.name, this.value)),
                        button -> client.setScreen(new SelectorScreen(screen)))
                .position(x, y)
                .width(width)
                .tooltip(Util.nullMap(getDescription(), Tooltip::of))
                .build());

        return 20;
    }

    @Override
    public void render(RenderData data, int x, int y) {

    }

    public interface OptionGetter {
        List<String> getOptions();
    }

    class SelectorScreen extends Screen {
        private final Screen parent;
        private SelectorWidget selectorWidget;

        protected SelectorScreen(Screen parent) {
            super(Text.of("Selector Setting"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            this.selectorWidget =
                    new SelectorWidget(this.client, SelectorSetting.this.optionGetter, this.width, this.height, 32,
                            this.height - 32, textRenderer.fontHeight + 8);
            this.addSelectableChild(selectorWidget);
            Util.addChild(this, ButtonWidget.builder(Text.of("Done"),
                            button -> Objects.requireNonNull(this.client).setScreen(this.parent))
                    .position(this.width / 2 - 100, this.height - 27)
                    .width(200)
                    .build());
        }

        @Override
        public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
            this.selectorWidget.render(matrices, mouseX, mouseY, delta);
            super.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public void close() {
            Objects.requireNonNull(client)
                    .setScreen(parent);
        }

        class SelectorWidget extends EntryListWidget<SelectorWidget.SelectorEntry> {
            public SelectorWidget(MinecraftClient client, OptionGetter optionGetter, int width, int height, int top, int bottom, int itemHeight) {
                super(client, width, height, top, bottom, itemHeight);
                optionGetter.getOptions()
                        .stream()
                        .map(SelectorEntry::new)
                        .forEach(this::addEntry);
                if (SelectorSetting.this.value != null)
                    super.setSelected(new SelectorEntry(SelectorSetting.this.value));
            }

            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {

            }

            @Override
            public void setSelected(@Nullable SelectorSetting.SelectorScreen.SelectorWidget.SelectorEntry entry) {
                if (entry != null) SelectorSetting.this.value = entry.value;
                super.setSelected(entry);
            }

            class SelectorEntry extends EntryListWidget.Entry<SelectorEntry> {
                private final String value;

                SelectorEntry(String value) {
                    this.value = value;
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj instanceof SelectorEntry) return ((SelectorEntry) obj).value.equals(this.value);
                    return false;
                }

                @Override
                public void render(DrawContext matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                    matrices.drawText(SelectorWidget.this.client.textRenderer, this.value,
                            (int) (width / 2f - client.textRenderer.getWidth(this.value) / 2f), y + 1, 16777215, true);
                }

                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    if (button != 0) return false;
                    SelectorWidget.this.setSelected(this);
                    return true;
                }
            }
        }
    }
}
