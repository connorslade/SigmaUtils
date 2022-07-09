package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Objects;

public class ChatSignatures extends Module {
    protected ChatSignatures() {
        super("No Chat Signatures", "Disables sending the chat signatures used for chat reporting", Category.Misc);
    }

    @Override
    public boolean getConfig() {
        return Config.chatSignatures;
    }

    @Override
    public void setConfig(boolean config) {
        Config.chatSignatures = config;
    }

    @Override
    public void enable(MinecraftClient client) {
        Objects.requireNonNull(client.player)
                .sendMessage(Text.of("CS ON"), true);
    }

    @Override
    public void disable(MinecraftClient client) {

    }
}
