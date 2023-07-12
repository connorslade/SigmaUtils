package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.modules.meta.Padding;
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

public class ConfigGui extends Screen {
    Screen _super;

    public ConfigGui() {
        super(Text.of("Sigma Utils - Config"));
    }

    public ConfigGui(Screen screen) {
        this();
        this._super = screen;
    }

    public static int getPadding() {
        try {
            if (!Config.getEnabled(Padding.class)) return 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Padding.padding.intValue();
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

            for (int y = 0; y < categoryModules.size(); y++) {
                Module module = categoryModules.get(y);
                int renderX = padding + x * (150 + padding);
                int renderY = textRenderer.fontHeight + padding * 2 + y * (20 + padding);
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
        int padding = getPadding();
        this.renderBackground(drawContext);

        for (int x = 0; x < Category.realValues().length; x++)
            drawContext.drawCenteredTextWithShadow(textRenderer,
                    Text.of("§f§n§l" + Category.realValues()[x].toString()),
                    75 + padding + x * (150 + padding), padding, 0);

        super.render(drawContext, mouseX, mouseY, delta);
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
