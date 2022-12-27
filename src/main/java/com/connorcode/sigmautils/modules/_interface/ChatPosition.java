package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ChatPosition extends BasicModule {
    public static NumberSetting yPosition = new NumberSetting(ChatPosition.class, "Y Position", 0, 10).precision(0)
            .description("The number of lines to offset the chat display by")
            .build();

    public ChatPosition() {
        super("chat_position", "Chat Position", "Lets you move the chat panel up", Category.Interface);
    }

    @Override
    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.sliderConfig(client, screen, x, y, this, yPosition);
    }
}
