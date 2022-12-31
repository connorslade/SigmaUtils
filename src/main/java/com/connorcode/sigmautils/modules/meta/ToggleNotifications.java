package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class ToggleNotifications extends Module {
    public static EnumSetting<Display> display = new EnumSetting<>(ToggleNotifications.class, "Display", Display.class)
            .description("Where to display the notification.")
            .value(Display.ACTION_BAR)
            .build();

    public ToggleNotifications() {
        super("toggle_notifications", "Toggle Notifications", "Shows a message in the (C)hat, (A)ction bar or (T)oast",
                Category.Meta);
    }

    public static void moduleEnable(MinecraftClient client, Module module) {
        if (client.player == null) return;
        switch (display.value()) {
            case CHAT -> client.player.sendMessage(Text.of(String.format("§aEnabled §d%s", module.name)), false);
            case ACTION_BAR -> client.player.sendMessage(Text.of(String.format("§aEnabled §d%s", module.name)), true);
            case TOAST -> client.getToastManager()
                    .add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.of("Module Enabled"),
                            Text.of(module.name)));
        }
    }

    public static void moduleDisable(MinecraftClient client, Module module) {
        if (client.player == null) return;
        switch (display.value()) {
            case CHAT -> client.player.sendMessage(Text.of(String.format("§cDisabled §d%s", module.name)), false);
            case ACTION_BAR -> client.player.sendMessage(Text.of(String.format("§cDisabled §d%s", module.name)), true);
            case TOAST -> client.getToastManager()
                    .add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.of("Module Disabled"),
                            Text.of(module.name)));
        }
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
