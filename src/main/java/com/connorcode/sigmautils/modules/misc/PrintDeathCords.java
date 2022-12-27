package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class PrintDeathCords extends Module {
    public static double[] lastDeath = null;

    public PrintDeathCords() {
        super("print_death_cords", "Print Death Cords", "Prints your coordinates on death", Category.Misc);
    }
}
