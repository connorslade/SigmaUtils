package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class BlockDistance extends Module {
    public static NumberSetting maxDistance =
            new NumberSetting(BlockDistance.class, "Max Distance", 100, 1000).value(500)
                    .description("The max distance to ray-trace to find the block distance.")
                    .precision(1)
                    .build();

    public BlockDistance() {
        super("block_distance", "Block Distance", "Displays the distance to the block you are looking at",
                Category.Misc);
    }
}
