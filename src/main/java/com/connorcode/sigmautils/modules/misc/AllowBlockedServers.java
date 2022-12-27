package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class AllowBlockedServers extends Module {
    public AllowBlockedServers() {
        super("allow_blocked_servers", "Allow Blocked Servers",
                "Allows you to join servers that have been blocked by Mojang.",
                Category.Misc);
    }
}
