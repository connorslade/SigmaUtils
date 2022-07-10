package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class ShowInvisibleEntities extends BasicModule {
    public ShowInvisibleEntities() {
        super("show_invisible_entities", "Show Invisible Entities", "Render all invisible entities",
                Category.Rendering);
    }
}
