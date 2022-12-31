package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.mixin.SimpleOptionAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

public class FullBright extends Module {
    double baseGamma;

    public FullBright() {
        super("full_bright", "Full Bright", "There shall be no darkness", Category.Rendering);
    }

    public void loadConfig(NbtCompound config) {
        super.loadConfig(config);
        baseGamma = config.getDouble("base_gamma");
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = super.saveConfig();
        nbt.putDouble("base_gamma", baseGamma);
        return nbt;
    }

    public void enable(MinecraftClient client) {
        super.enable(client);
        baseGamma = client.options.getGamma()
                .getValue();
        ((SimpleOptionAccessor) (Object) client.options.getGamma()).setValue(15d);
    }

    public void disable(MinecraftClient client) {
        super.disable(client);
        ((SimpleOptionAccessor) (Object) client.options.getGamma()).setValue(baseGamma);
    }
}