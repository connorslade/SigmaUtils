package com.connorcode.sigmautils.config.settings;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;

public class KeybindSetting extends Setting {


    public KeybindSetting(Class module, String name) {
        super(module, name);
    }

    @Override
    public void serialize(NbtCompound nbt) {

    }

    @Override
    public void deserialize(NbtCompound nbt) {

    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        return 0;
    }

    @Override
    public void render(RenderData data, int x, int y) {

    }
}
