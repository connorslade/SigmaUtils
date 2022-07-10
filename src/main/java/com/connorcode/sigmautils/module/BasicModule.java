package com.connorcode.sigmautils.module;

import com.connorcode.sigmautils.misc.Util;
import net.minecraft.nbt.NbtCompound;

public class BasicModule extends Module {
    protected BasicModule(String id, String name, String description, Category category) {
        super(id, name, description, category);
    }

    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
    }

    public NbtCompound saveConfig() {
        return Util.saveEnabled(enabled);
    }
}
