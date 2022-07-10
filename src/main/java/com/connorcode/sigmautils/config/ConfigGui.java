package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class ConfigGui extends Screen {
    final int PADDING = 2;

    public ConfigGui() {
        super(Text.of("Sigma Utils - Config"));
    }

    protected void init() {
        // Draw Module toggles
        for (int x = 0; x < Category.values().length; x++) {
            Category category = Category.values()[x];
            List<Module> categoryModules = Arrays.stream(SigmaUtilsClient.modules)
                    .filter(m -> m.category == category)
                    .toList();

            for (int y = 0; y < categoryModules.size(); y++) {
                Module module = categoryModules.get(y);
                int renderX = PADDING + x * (150 + PADDING);
                int renderY = textRenderer.fontHeight + PADDING * 2 + y * (20 + PADDING);
                addDrawableChild(new ButtonWidget(renderX, renderY, 150, 20,
                        Text.of(String.format("%s█§r %s", module.enabled ? "§a" : "§c", module.name)), button -> {
                    boolean newState = !module.enabled;
                    if (newState) module.enable(client);
                    else module.disable(client);
                    module.enabled = newState;
                    try {
                        Config.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    clearAndInit();
                }, ((button, matrices, mouseX, mouseY) -> renderOrderedTooltip(matrices,
                        textRenderer.wrapLines(Text.of(module.description), 200), mouseX, mouseY))));
            }
        }

        // GitHub Link
        int githubLen = textRenderer.getWidth("Github");
        addDrawableChild(
                new ButtonWidget(width - githubLen - PADDING * 5, height - 20 - PADDING, githubLen + PADDING * 4, 20,
                        Text.of("Github"), button -> {
                    try {
                        Util.getOperatingSystem()
                                .open(new URI("https://github.com/basicprogrammer10"));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        for (int x = 0; x < Category.values().length; x++)
            drawCenteredText(matrices, textRenderer, Text.of("§f§n§l" + Category.values()[x].toString()),
                    75 + PADDING + x * (150 + PADDING), PADDING, 0);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
