package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class ShowInvisibleEntities extends Module {
    public ShowInvisibleEntities() {
        super("show_invisible_entities", "Show Invisible Entities", "Render all invisible entities",
                Category.Rendering);
    }
}
