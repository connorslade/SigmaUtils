package com.connorcode.sigmautils.module;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Util;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public abstract class BasicModule extends Module {
    protected BasicModule(String id, String name, String description, Category category) {
        super(id, name, description, category);
    }

    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
        Config.moduleSettings.getOrDefault(getClass(), List.of())
                .forEach(setting -> setting.deserialize(config));
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = Util.saveEnabled(enabled);
        Config.moduleSettings.getOrDefault(getClass(), List.of())
                .forEach(setting -> setting.serialize(nbt));
        return nbt;
    }
}
