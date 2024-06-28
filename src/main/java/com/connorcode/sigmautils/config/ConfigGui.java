package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.modules.meta.Padding.getPadding;

public class ConfigGui extends Screen {
    Screen _super;

    public ConfigGui() {
        super(Text.of("Sigma Utils - Config"));
    }

    public ConfigGui(Screen screen) {
        this();
        this._super = screen;
    }

    protected void init() {
        int padding = getPadding();

        // Draw Module toggles
        for (int x = 0; x < Category.realValues().length; x++) {
            Category category = Category.realValues()[x];
            List<Module> categoryModules = SigmaUtils.modules.values().stream()
                    .filter(m -> m.category == category)
                    .sorted(Comparator.comparing(m -> m.name))
                    .toList();

            int y = 0;
            for (var module : categoryModules) {
                if (module.inDevelopment && !FabricLoader.getInstance().isDevelopmentEnvironment()) continue;
                int renderX = padding + x * (150 + padding);
                int renderY = textRenderer.fontHeight + padding * 2 + y++ * (20 + padding);
                module.drawInterface(client, this, renderX, renderY);
            }
        }

        // GitHub Link
        int githubLen = textRenderer.getWidth("Github");
        addDrawableChild(ButtonWidget.builder(Text.of("Github"), b -> {
            try {
                Util.getOperatingSystem()
                        .open(new URI("https://github.com/Basicprogrammer10/SigmaUtils"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }).position(width - githubLen - padding * 5, height - 20 - padding).size(githubLen + padding * 4, 20).build());
    }

    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext, mouseX, mouseY, delta);

        int padding = getPadding();
        super.render(drawContext, mouseX, mouseY, delta);

        for (int x = 0; x < Category.realValues().length; x++)
            drawContext.drawCenteredTextWithShadow(textRenderer,
                    Text.of("§f§n§l" + Category.realValues()[x].toString()),
                    75 + padding + x * (150 + padding), padding, 0);
    }

    public void close() {
        Objects.requireNonNull(client).setScreen(_super);
        try {
            Config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
