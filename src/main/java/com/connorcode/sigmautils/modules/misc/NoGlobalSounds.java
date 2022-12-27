package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoGlobalSounds extends Module {
    public NoGlobalSounds() {
        super("no_global_sounds", "No Global Sounds",
                "Disables playing all global sounds. (Wither Spawn, End Portal Open, Dragon Death)", Category.Misc);
    }
}
