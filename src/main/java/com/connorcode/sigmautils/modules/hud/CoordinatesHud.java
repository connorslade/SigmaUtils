package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class CoordinatesHud extends BasicModule {
    public CoordinatesHud() {
        super("coordinates_hud", "Coordinates Hud", "Adds a display on your screen showing your current location",
                Category.Hud);
    }
}
