package com.connorcode.sigmautils.module;

import java.util.List;

public abstract class HudModule extends BasicModule {
    protected HudModule(String id, String name, String description, Category category) {
        super(id, name, description, category);
    }

    public String line() {
        return "";
    }

    public List<String> lines() {
        return List.of(line());
    }
}
