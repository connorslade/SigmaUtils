package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.ModuleInfo;
import com.connorcode.sigmautils.module.Module;

@ModuleInfo(description = "Prints your coordinates on death")
public class PrintDeathCords extends Module {
    public static double[] lastDeath = null;
}
