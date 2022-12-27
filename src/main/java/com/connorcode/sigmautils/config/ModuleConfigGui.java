package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.config.settings.Setting;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;
import static com.connorcode.sigmautils.modules.meta.Scale.getScale;

public class ModuleConfigGui extends Screen {
    private final Module module;
    // Maps <Category, List<Setting>>
    private final HashMap<String, List<Setting>> settings = new HashMap<>();

    public ModuleConfigGui(Module module) {
        super(Text.of("Sigma Utils - Module Config"));
        this.module = module;
    }

    @Override
    protected void init() {
        int padding = getPadding();
        this.settings.clear();
        List<Setting> settings = Config.moduleSettings.getOrDefault(module.getClass(), List.of());
        for (Setting setting : settings) {
            String category = setting.getCategory();
            this.settings.putIfAbsent(category, new ArrayList<>());
            this.settings.get(category)
                    .add(setting);
        }

        List<List<Setting>> settingsList = new ArrayList<>(this.settings.values());
        System.out.println(settingsList);
        for (int x = 0; x < settingsList.size(); x++) {
            for (int y = 0; y < settingsList.get(x)
                    .size(); y++) {
                int xPos = padding + x * (150 + padding);
                int yPos = textRenderer.fontHeight * 2 + padding * 4 + y * (20 + padding);

                settingsList.get(x)
                        .get(y)
                        .initRender(this, xPos, yPos);
            }
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance()
                .setScreen(new ConfigGui());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Setting.RenderData renderData = new Setting.RenderData(this, matrices, mouseX, mouseY, delta);
        int padding = getPadding();
        float scale = getScale();

        this.renderBackground(matrices);
        matrices.scale(scale, scale, scale);

        super.render(matrices, mouseX, mouseY, delta);

        int x = 0;
        for (Map.Entry<String, List<Setting>> entry : settings.entrySet()) {
            x += 100;
            String category = entry.getKey();
            if (category != null)
                drawCenteredText(matrices, textRenderer, Text.of(category), x * 100, padding, 0xFFFFFF);

            List<Setting> categorySettings = entry.getValue();
            for (int i = 0; i < categorySettings.size(); i++) {
                Setting setting = categorySettings.get(i);
                int renderX = padding + i * (150 + padding);
                int renderY = textRenderer.fontHeight + padding * 2 + i * (20 + padding);
                setting.render(renderData, renderX, renderY);
            }
        }

        drawCenteredText(matrices, textRenderer, Text.of(String.format("§f§n§l%s Settings", module.name)), width / 2,
                padding, 0);
    }
}
