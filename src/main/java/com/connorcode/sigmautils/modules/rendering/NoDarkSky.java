package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoDarkSky extends Module {
    public NoDarkSky() {
        super("no_dark_sky", "No Dark Sky", "Disables the black sky effect when your camera is under sea level",
                Category.Rendering);
    }
}

// TODO: I belive this still needs to be fixed