package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;

import java.util.Arrays;
import java.util.stream.Collectors;

@ModuleInfo(description = "Modifies your outgoing chat messages to use maximum egg puns.",
        documentation = "its really funny i swear")
public class EggChat extends Module {
    public static String eggify(String inp) {
        return Arrays.stream(inp.split(" "))
                .map(w -> {
                    if (w.contains("ex") && !w.endsWith("ex")) return w.replace("ex", "egg");
                    return w;
                })
                .collect(Collectors.joining(" "));
    }
}
