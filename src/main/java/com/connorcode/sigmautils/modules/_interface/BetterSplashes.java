package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

@ModuleInfo(description = "Adds a lot of custom splashes :)")
public class BetterSplashes extends Module {
    public static final List<String> betterSplashes =
            new BufferedReader(new InputStreamReader(Objects.requireNonNull(SigmaUtils.class.getClassLoader()
                    .getResourceAsStream("assets/sigma-utils/splashes.txt")))).lines()
                    .toList();
}
