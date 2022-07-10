package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.mixin.SimpleOptionAccessor;
import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;
import net.minecraft.client.MinecraftClient;

public class FullBright extends BasicModule {
    double baseGamma;

    public FullBright() {
        super("full_bright", "Full Bright", "There shall be no darkness", Category.Rendering);
    }

    public void enable(MinecraftClient client) {
        baseGamma = client.options.getGamma()
                .getValue();
        ((SimpleOptionAccessor) (Object) client.options.getGamma()).setValue(15d);
    }

    public void disable(MinecraftClient client) {
        ((SimpleOptionAccessor) (Object) client.options.getGamma()).setValue(baseGamma);
    }
}