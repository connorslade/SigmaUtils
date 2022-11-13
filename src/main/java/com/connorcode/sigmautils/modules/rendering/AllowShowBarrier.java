package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class AllowShowBarrier extends BasicModule {
    public AllowShowBarrier() {
        super("allow_show_barrier", "Allow Show Barrier", "Allows for showing the barrier / light particle when not in creative or holding the item", Category.Rendering);
    }
}
