package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.config.settings.Setting;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class ModuleConfigGui extends Screen {
    private final Module module;
    private final Screen parent;
    // Maps <Category, List<Setting>>
    private final List<Pair<String, List<Setting<?>>>> settings = new ArrayList<>();
    private final HashMap<String, Integer> elementPositions = new HashMap<>();

    public ModuleConfigGui(Module module, Screen parent) {
        super(Text.of("Sigma Utils - Module Config"));
        this.module = module;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int padding = getPadding();
        this.settings.clear();
        List<Setting<?>> settings = Config.moduleSettings.getOrDefault(module.getClass(), List.of());
        for (Setting<?> setting : settings) {
            String category = setting.getCategory();
            // Maybe: Clean this up
            if (this.settings.stream().noneMatch(pair -> pair.getLeft().equals(category)))
                this.settings.add(new Pair<>(category, new ArrayList<>()));
            this.settings.stream()
                    .filter(pair -> pair.getLeft().equals(category))
                    .findFirst()
                    .orElseThrow()
                    .getRight()
                    .add(setting);
        }

        // Add an 'enabled' setting
        Util.addChild(this,
                ButtonWidget.builder(Text.of(String.format("%s█§r Enabled", module.enabled ? "§a" : "§c")), button -> {
                            if (!module.enabled) module.enable();
                            else module.disable();
                            this.clearAndInit();
                        })
                        .position(20 + padding, textRenderer.fontHeight * 2 + padding * 4)
                        .size(150 - padding, 20)
                        .tooltip(Util.nullMap(module.description, d -> Tooltip.of(Text.of(d))))
                        .build());

        List<List<Setting<?>>> settingsList = this.settings.stream().map(Pair::getRight).toList();
        for (int x = 0; x < settingsList.size(); x++) {
            int xPos = 20 + padding + x * (150 + padding);
            int yPos = textRenderer.fontHeight * 2 + padding * 4;

            for (int y = 0; y < settingsList.get(x).size(); y++) {
                elementPositions.put(settingsList.get(x).get(y).getName(), yPos);
                yPos += settingsList.get(x).get(y).initRender(this, xPos, yPos, 150 - padding) + padding;
            }
        }
    }

    @Override
    public void close() {
        for (List<Setting<?>> settings : this.settings.stream().map(Pair::getRight).toList())
            for (Setting<?> setting : settings)
                setting.onClose();
        Objects.requireNonNull(client).setScreen(parent);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        Setting.RenderData renderData = new Setting.RenderData(this, drawContext, mouseX, mouseY, delta);
        int padding = getPadding();

        this.renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);

        int x = -75 + 20 + padding;
        for (Pair<String, List<Setting<?>>> entry : settings) {
            x += 150 + padding;
            if (entry.getLeft() != null)
                drawContext.drawCenteredTextWithShadow(textRenderer,
                        Text.of(String.format("§f§n§l%s", entry.getLeft())),
                        x, padding * 2 + textRenderer.fontHeight, 0);

            for (Setting<?> setting : entry.getRight())
                setting.render(renderData, x - 75, elementPositions.get(setting.getName()));
        }

        drawContext.drawCenteredTextWithShadow(textRenderer, Text.of(String.format("§f§n§l%s Settings", module.name)),
                width / 2,
                padding, 0);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean cancel = false;
        for (List<Setting<?>> settingList : settings.stream().map(Pair::getRight).toList())
            for (Setting<?> setting : settingList)
                cancel |= setting.onKeypress(keyCode, scanCode, modifiers);
        return cancel || super.keyPressed(keyCode, scanCode, modifiers);
    }
}
