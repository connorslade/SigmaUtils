package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class FlippedEntities extends BasicModule {
    public FlippedEntities() {
        super("flipped_entities", "Flipped Entities", "Always render entities upside down!", Category.Rendering);
    }
}
