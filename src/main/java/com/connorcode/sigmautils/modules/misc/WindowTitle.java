package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;

import static com.connorcode.sigmautils.SigmaUtils.VERSION;
import static com.connorcode.sigmautils.SigmaUtils.client;

public class WindowTitle extends Module {
    private static final NumberSetting refreshPeriod =
            new NumberSetting(WindowTitle.class, "Refresh Period", 1, 20).value(20)
                    .description("The number of ticks between each refresh of the window title.")
                    .build();
    private static final StringSetting titleFormatter = new StringSetting(WindowTitle.class, "Title Formatter").value(
                    "Sigma Utils v{version} | {server} | {player}")
            .build();
    private static long lastRefresh = 0;
    private static String lastTitle = "";

    public WindowTitle() {
        super("window_title", "Window Title", "Changes the window title", Category.Misc);
    }

    public static String getTitle() {
        if (lastRefresh + refreshPeriod.intValue() >= System.currentTimeMillis()) return lastTitle;
        lastRefresh = System.currentTimeMillis();

        // TODO: Formatter system
        // Merge /util run formatted into a new system that returns a List<String> of permutations
        // In this module we can just pick the first one
        // Maybe add a parameter to the formatter to an/disable permutations
        // Then just add more formatters like {server} and {player.*}
        lastTitle = titleFormatter.value()
                .replace("{version}", VERSION)
                .replace("{server}",
                        client.getCurrentServerEntry() ==
                                null ? "Singleplayer" : client.getCurrentServerEntry().address)
                .replace("{player}", client.player == null ? "Offline" : client.player.getName()
                        .getString());
        return lastTitle;
    }

    @Override
    public void tick() {
        super.tick();

        if (System.currentTimeMillis() + refreshPeriod.intValue() >= lastRefresh) return;
        client.updateWindowTitle();
    }

    @Override
    public void enable(MinecraftClient client) {
        super.enable(client);
        client.updateWindowTitle();
    }

    @Override
    public void disable(MinecraftClient client) {
        super.disable(client);
        client.updateWindowTitle();
    }
}
