package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoArmor extends BasicModule {
    public NoArmor() {
        super("no_armor", "No Armor", "Dont render armor. Its that simple.", Category.Rendering);
    }
}
