package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class AllowShowBarrier extends Module {
    public AllowShowBarrier() {
        super("allow_show_barrier", "Allow Show Barrier",
                "Allows for showing the barrier / light particle when not in creative or holding the item",
                Category.Rendering);
    }
}
