package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.config.settings.Setting;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Setting> settings = Config.moduleSettings.getOrDefault(module.getClass(), List.of());
        for (Setting setting : settings) {
            String category = setting.getCategory();
            this.settings.putIfAbsent(category, List.of());
            this.settings.get(category)
                    .add(setting);
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance()
                .setScreen(new ConfigGui());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int padding = ConfigGui.getPadding();
        float scale = getScale();

        this.renderBackground(matrices);
        matrices.scale(scale, scale, scale);

        for (Map.Entry<String, List<Setting>> entry : settings.entrySet()) {
            String category = entry.getKey();
            drawCenteredText(matrices, textRenderer, Text.of(category), width / 2, padding, 0xFFFFFF);

            List<Setting> categorySettings = entry.getValue();
            for (int i = 0; i < categorySettings.size(); i++) {
                Setting setting = categorySettings.get(i);
                int renderX = padding + i * (150 + padding);
                int renderY = textRenderer.fontHeight + padding * 2 + i * (20 + padding);
                setting.render(new Setting.RenderData(matrices, mouseX, mouseY, delta), renderX, renderY);
            }
        }
    }
}
