package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoTelemetry extends BasicModule {
    public NoTelemetry() {
        super("no_telemetry", "No Telemetry", "Disables the builtin telemetry system added in 1.18", Category.Misc);
    }
}
