package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class DisableFrontPerspective extends Module {
    public DisableFrontPerspective() {
        super("disable_front_perspective", "No Front Perspective",
                "Removes the front perspective from perspective switching", Category.Misc);
    }
}
