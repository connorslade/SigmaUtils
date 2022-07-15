package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class DisableFrontPerspective extends BasicModule {
    public DisableFrontPerspective() {
        super("disable_front_perspective", "No Front Perspective",
                "Removes the front perspective from perspective switching", Category.Misc);
    }
}
