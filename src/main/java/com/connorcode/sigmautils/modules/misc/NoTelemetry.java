package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoTelemetry extends Module {
    public NoTelemetry() {
        super("no_telemetry", "No Telemetry", "Disables the builtin telemetry system added in 1.18", Category.Misc);
    }
}
