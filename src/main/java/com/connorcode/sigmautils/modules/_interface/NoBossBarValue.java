package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoBossBarValue extends Module {
    public NoBossBarValue() {
        super("no_boss_bar_value", "No Boss Bar Value", "Removes the boss bar, leaving only the text",
                Category.Interface);
    }
}
