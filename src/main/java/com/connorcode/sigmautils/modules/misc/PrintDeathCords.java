package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;

@ModuleInfo(description = "Prints your coordinates on death")
public class PrintDeathCords extends Module {
    public static double[] lastDeath = null;
}
