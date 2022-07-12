package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class BlockDistance extends BasicModule {
    public BlockDistance() {
        super("block_distance", "Block Distance", "Displays the distance to the block you are looking at",
                Category.Misc);
    }
}
