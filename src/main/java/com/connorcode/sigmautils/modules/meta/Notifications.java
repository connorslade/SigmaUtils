package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.module.ModuleInfo;
import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

@ModuleInfo(description = "Shows a message in the (C)hat, (A)ction bar or (T)oast for various events.")
public class Notifications extends Module {
    private static final EnumSetting<Display> display =
            new EnumSetting<>(Notifications.class, "Display", Display.class).description(
                    "Where to display the notification.").value(Display.ACTION_BAR).build();

    private static final BoolSetting module =
            new BoolSetting(Notifications.class, "Module").description("Show a notification when a module is toggled.")
                    .value(true)
                    .build();

    private static final BoolSetting startup =
            new BoolSetting(Notifications.class, "Startup").description("Show a notification when Sigma Utils starts.")
                    .value(false)
                    .build();

    public static void moduleEnable(MinecraftClient client, Module module) {
        if (client.player == null || !Notifications.module.value()) return;
        switch (display.value()) {
            case CHAT -> client.player.sendMessage(Text.of(String.format("§aEnabled §d%s", module.name)), false);
            case ACTION_BAR -> client.player.sendMessage(Text.of(String.format("§aEnabled §d%s", module.name)), true);
            case TOAST -> client.getToastManager()
                    .add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.of("Module Enabled"),
                            Text.of(module.name)));
        }
    }

    public static void moduleDisable(MinecraftClient client, Module module) {
        if (client.player == null || !Notifications.module.value()) return;
        switch (display.value()) {
            case CHAT -> client.player.sendMessage(Text.of(String.format("§cDisabled §d%s", module.name)), false);
            case ACTION_BAR -> client.player.sendMessage(Text.of(String.format("§cDisabled §d%s", module.name)), true);
            case TOAST -> client.getToastManager()
                    .add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.of("Module Disabled"),
                            Text.of(module.name)));
        }
    }

    public static void onStartup() {
        if (!Notifications.startup.value() || Notifications.display.value() != Display.TOAST) return;
        client
                .getToastManager()
                .add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.of("Sigma Utils"),
                        Text.of("Started V" + SigmaUtils.VERSION)));
    }

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.addToggleButton(screen, this, x, y, 130 - getPadding(), false);
        Components.enumConfig(screen, x, y, display, new char[]{
                'C',
                'A',
                'T'
        });
    }

    public enum Display {
        CHAT,
        ACTION_BAR,
        TOAST
    }
}
