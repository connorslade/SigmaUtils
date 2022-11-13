package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class AllowBlockedServers extends BasicModule {
    public AllowBlockedServers() {
        super("allow_blocked_servers", "Allow Blocked Servers",
                "Allows you to join servers that have been blocked by Mojang.",
                Category.Misc);
    }
}
