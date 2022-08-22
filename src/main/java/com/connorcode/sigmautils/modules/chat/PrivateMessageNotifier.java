package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.misc.Formatter;
import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.MessageHeaderS2CPacket;
import net.minecraft.text.Text;

import java.util.Objects;

public class PrivateMessageNotifier extends BasicModule {
    public PrivateMessageNotifier() {
        super("private_message_notifier", "Private Message Notifier",
                "Shows a chat alert when a private message is detected. This is a byproduct of the chat signing system.",
                Category.Chat);
    }

    @Override
    public void init() {
        PacketReceiveCallback.EVENT.register(packet -> {
            if (!(packet instanceof MessageHeaderS2CPacket header && enabled)) return false;
            PlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().world)
                    .getPlayerByUuid(header.header()
                            .sender());

            if (player == null) return false;
            Objects.requireNonNull(MinecraftClient.getInstance().player)
                    .sendMessage(Text.of(new Formatter("Σ] `{}` just sent a message.", player.getGameProfile()
                            .getName()).format()));
            return false;
        });
    }
}
