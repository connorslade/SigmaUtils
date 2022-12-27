package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoScoreboardValue extends Module {
    public NoScoreboardValue() {
        super("no_scoreboard_value", "No Scoreboard Value",
                "Disables rendering the red values on the scoreboard sidebar", Category.Interface);
    }
}
