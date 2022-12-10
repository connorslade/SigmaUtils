package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoPause extends BasicModule {
    public NoPause() {
        super("no_pause", "No Pause",
                "Keeps the game from pausing in singleplayer.",
                Category.Misc);
    }
}
