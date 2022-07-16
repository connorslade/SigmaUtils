package com.connorcode.sigmautils.module;

public abstract class HudModule extends BasicModule {
    protected HudModule(String id, String name, String description, Category category) {
        super(id, name, description, category);
    }

    public abstract String line();
}
