package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;

@ModuleInfo(description = "Makes clicking signs / item frames in front of clickable blocks click the block.",
        documentation = "Nice for storage systems that use item frames or signs on the front of the chests. If you do want to interact with the Item Frame or Sign, you can hold shift while right clicking.")
public class SignClickThrough extends Module {}
