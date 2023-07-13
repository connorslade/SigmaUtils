package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;

@ModuleInfo(description = "Removes the effect where messages fade out after 10 seconds",
        documentation = "Allows you to read older messages if not too many are sent.")
public class NoChatFade extends Module {}
