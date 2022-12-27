package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class EscapeExit extends Module {
    public EscapeExit() {
        super("escape_exit", "Escape Exit", "Closes the game when you press ESC on the title screen",
                Category.Interface);
    }
}
