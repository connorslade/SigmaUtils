package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class EscapeExit extends BasicModule {
    public EscapeExit() {
        super("escape_exit", "Escape Exit", "Closes the game when you press ESC on the title screen",
                Category.Interface);
    }
}
