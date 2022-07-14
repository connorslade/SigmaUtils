package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoScoreboardValue extends BasicModule {
    public NoScoreboardValue() {
        super("no_scoreboard_value", "No Scoreboard Value", "Disables rendering the red values on the scoreboard sidebar", Category.Interface);
    }
}
