package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoGlobalSounds extends BasicModule {
    public NoGlobalSounds() {
        super("no_global_sounds", "No Global Sounds", "Disables playing all global sounds. (Wither Spawn, End Portal Open, Dragon Death)", Category.Misc);
    }
}
