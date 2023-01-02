package com.connorcode.sigmautils.module;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.config.settings.Setting;
import com.connorcode.sigmautils.misc.TextStyle;

import java.util.List;

public abstract class HudModule extends Module {
    public NumberSetting order;
    protected int defaultOrder;
    protected EnumSetting<TextStyle.Color> textColor;
    protected TextStyle.Color defaultTextColor = TextStyle.Color.Green;

    protected HudModule(String id, String name, String description, Category category) {
        super(id, name, description, category);
    }

    public String line() {
        return "";
    }

    public List<String> lines() {
        return List.of(line());
    }

    protected String getTextColor() {
        return textColor.value().code;
    }

    @Override
    public void init() {
        super.init();
        textColor = new EnumSetting<>(this.getClass(), "Color", TextStyle.Color.class).value(defaultTextColor)
                .description("The color of the text")
                .category("Style")
                .build();
        order = new NumberSetting(this.getClass(), "Order", 0, 20).description("The order of the hud module")
                .precision(0)
                .value(defaultOrder)
                .build();
        List<Setting<?>> moduleSettings = Config.moduleSettings.get(this.getClass());
        moduleSettings.remove(order);
        moduleSettings.add(2, order);
    }
}
