package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.module.Module;

import java.util.ArrayList;

public class NumberSetting implements Setting {
    private final String name;
    private final double min;
    private final double max;
    private String category;
    private double increment;
    private double value;

    public <T extends Module> NumberSetting(Class<T> module, String name, double min, double max, double increment) {
        this.name = name;
        this.min = min;
        this.max = max;

        Config.moduleSettings.putIfAbsent(module, new ArrayList<>());
    }

    public NumberSetting category(String category) {
        this.category = category;
        return this;
    }

    public NumberSetting increment(double increment) {
        this.increment = increment;
        return this;
    }

    public NumberSetting value(double value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public void render(RenderData data, int x, int y) {

    }
}
