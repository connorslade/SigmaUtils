package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoBossBarValue extends BasicModule {
    public NoBossBarValue() {
        super("no_boss_bar_value", "No Boss Bar Value", "Removes the boss bar, leaving only the text", Category.Interface);
    }
}
