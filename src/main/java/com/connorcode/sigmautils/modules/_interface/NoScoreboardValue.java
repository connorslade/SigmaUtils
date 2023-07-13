package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;

@ModuleInfo(description = "Disables rendering the red values on the scoreboard sidebar",
        documentation = "Good for minigame servers that use the scoreboard values just to sort the scoreboard entries in the right order.")
public class NoScoreboardValue extends Module {}
