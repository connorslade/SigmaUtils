package com.connorcode.sigmautils.config;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Config {
    static final KeyBinding configKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.sigma-utils.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "key.category.sigma-utils"));
    public static boolean chatSignatures;
    public static boolean betterSplashes;
    public static boolean randomBackground;

    public static void initKeybindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKeybinding.wasPressed()) client.setScreen(new ConfigGui());
        });
    }
}
