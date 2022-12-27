package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoPause extends Module {
    public NoPause() {
        super("no_pause", "No Pause",
                "Keeps the game from pausing in singleplayer.",
                Category.Misc);
    }
}
