package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.modules.meta.Padding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.modules.meta.Scale.getScale;

public class ConfigGui extends Screen {
    public ConfigGui() {
        super(Text.of("Sigma Utils - Config"));
    }

    public static int getPadding() {
        try {
            if (!Config.getEnabled("padding")) return 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Padding.PADDING;
    }

    protected void init() {
        int padding = getPadding();

        // Draw Module toggles
        for (int x = 0; x < Category.values().length; x++) {
            Category category = Category.values()[x];
            List<Module> categoryModules = SigmaUtilsClient.modules.stream()
                    .filter(m -> m.category == category)
                    .toList();

            for (int y = 0; y < categoryModules.size(); y++) {
                Module module = categoryModules.get(y);
                int renderX = padding + x * (150 + padding);
                int renderY = textRenderer.fontHeight + padding * 2 + y * (20 + padding);
                module.drawConfigInterface(MinecraftClient.getInstance(), this, renderX, renderY);
            }
        }

        // Draw category buttons

        // GitHub Link
        int githubLen = textRenderer.getWidth("Github");
        addDrawableChild(
                new ButtonWidget(width - githubLen - padding * 5, height - 20 - padding, githubLen + padding * 4, 20,
                        Text.of("Github"), button -> {
                    try {
                        Util.getOperatingSystem()
                                .open(new URI("https://github.com/Basicprogrammer10/SigmaUtils"));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int padding = getPadding();
        float scale = getScale();

        this.renderBackground(matrices);
        matrices.scale(scale, scale, scale);

        for (int x = 0; x < Category.values().length; x++)
            drawCenteredText(matrices, textRenderer, Text.of("§f§n§l" + Category.values()[x].toString()),
                    75 + padding + x * (150 + padding), padding, 0);

        super.render(matrices, (int) ((float) mouseX / scale), (int) ((float) mouseY / scale), delta);
    }

    public void close() {
        Objects.requireNonNull(client)
                .setScreen(null);
        try {
            Config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
