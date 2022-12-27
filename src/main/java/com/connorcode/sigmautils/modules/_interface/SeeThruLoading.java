package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class SeeThruLoading extends Module {
    public SeeThruLoading() {
        super("see_thru_loading", "See Thru Loading",
                "Lets you see the world while it is being loaded in. You can even exit the loading screen with esc.",
                Category.Interface);
    }
}