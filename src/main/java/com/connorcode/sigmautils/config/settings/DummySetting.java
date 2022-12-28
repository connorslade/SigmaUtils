package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;

public class DummySetting extends Setting<DummySetting> {
    final int height;

    public DummySetting(Class<? extends Module> module, String name, int height) {
        super(module, name);
        this.height = height;
    }

    public DummySetting build() {
        Config.moduleSettings.putIfAbsent((Class<Module>) this.module, new ArrayList<>());
        Config.moduleSettings.get(this.module)
                .add(this);
        return this;
    }

    @Override
    public void serialize(NbtCompound nbt) {

    }

    @Override
    public void deserialize(NbtCompound nbt) {

    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        return height;
    }

    @Override
    public void render(RenderData data, int x, int y) {

    }
}
