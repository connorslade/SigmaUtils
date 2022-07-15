package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class BetterSplashes extends BasicModule {
    public static List<String> betterSplashes = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
            SigmaUtilsClient.class.getClassLoader()
                    .getResourceAsStream("splashes.txt")))).lines()
            .toList();

    public BetterSplashes() {
        super("better_splashes", "Better Splashes", "Adds alot of custom splashes :)", Category.Interface);
    }
}
