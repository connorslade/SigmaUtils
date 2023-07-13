package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.module.Category;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Objects;

public class ConfigGui2 extends Screen {
    Screen _super;
    Category category;

    public ConfigGui2(Screen _super) {
        super(Text.of("Sigma Utils - Config"));
        this._super = _super;
        this.category = Category.Interface;
    }

    public ConfigGui2(Screen _super, Category category) {
        super(Text.of("Sigma Utils - Config"));
        this._super = _super;
        this.category = category;
    }

    @Override
    protected void init() {
        // Category buttons
        for (var i : Category.realValues()) {
            var name = i.toString();
            if (i == category) name = "> " + name + " <";
            var button = ButtonWidget.builder(Text.of(name),
                            b -> Objects.requireNonNull(client).setScreen(new ConfigGui2(_super, i)))
                    .size(100, 20)
                    .position(4, 4 + i.ordinal() * 22)
                    .build();
            addDrawableChild(button);
        }

        // Modules
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(context);
        super.render(context, mouseX, mouseY, delta);
        context.fill(22, 0, width, height, 0x00000017);
    }
}