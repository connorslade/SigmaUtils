package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoBreakParticles extends BasicModule {
    public NoBreakParticles() {
        super("no_break_particles", "No Break Particles", "Disables rendering block break particles",
                Category.Rendering);
    }
}
