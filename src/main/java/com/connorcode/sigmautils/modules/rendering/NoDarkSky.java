package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoDarkSky extends BasicModule {
    public NoDarkSky() {
        super("no_dark_sky", "No Dark Sky", "Disables the black sky effect when your camera is under y63",
                Category.Rendering);
    }
}
