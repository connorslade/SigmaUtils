package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EggChat extends BasicModule {
    public EggChat() {
        super("egg_chat", "Egg Chat", "Modifies your outgoing chat messages to use maximum egg puns", Category.Misc);
    }

    public static String eggify(String inp) {
        return Arrays.stream(inp.split(" "))
                .map(w -> {
                    if (w.contains("ex") && !w.endsWith("ex")) return w.replace("ex", "egg");
                    return w;
                })
                .collect(Collectors.joining(" "));
    }
}
