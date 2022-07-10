package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoFog extends BasicModule {
    public NoFog() {
        super("no_fog", "No Fog", "Removes fog rendering", Category.Rendering);
    }
}
