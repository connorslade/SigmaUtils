package com.connorcode.sigmautils.module;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.modules.hud.Hud;

import java.util.List;

public abstract class HudModule extends Module {
    public NumberSetting order;
    public EnumSetting<Location> location;
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
        return textColor.value().code();
    }

    @Override
    public void init() {
        super.init();
        Hud.hudModules.add(this);
        textColor = new EnumSetting<>(this.getClass(), "Color", TextStyle.Color.class).value(defaultTextColor)
                .description("The color of the text")
                .category("Hud")
                .build();
        order = new NumberSetting(this.getClass(), "Order", 0, 20).value(defaultOrder)
                .description("The order of the hud module")
                .precision(0)
                .category("Hud")
                .build();
        location = new EnumSetting<>(this.getClass(), "Location", Location.class).value(Location.Auto)
                .description("The location of this hud element. Auto will inherit the location from the 'Hud' module")
                .category("Hud")
                .build();
    }

    public enum Location {
        Auto,
        TopLeft,
        TopRight,
        BottomRight,
        BottomLeft
    }
}
