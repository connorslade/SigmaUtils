package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class PrintDeathCords extends BasicModule {
    public static double[] lastDeath = null;

    public PrintDeathCords() {
        super("print_death_cords", "Print Death Cords", "Prints your coordinates on death", Category.Misc);
    }
}
