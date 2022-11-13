package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class SeeThruLoading extends BasicModule {
    public SeeThruLoading() {
        super("see_thru_loading", "See Thru Loading",
                "Lets you see the world while it is being loaded in. You can even exit the loading screen with esc.",
                Category.Interface);
    }
}
