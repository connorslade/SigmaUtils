package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class DisableShadows extends BasicModule {
    public DisableShadows() {
        super("disable_shadows", "Disable Shadows", "Disables all entity shadows", Category.Rendering);
    }
}
