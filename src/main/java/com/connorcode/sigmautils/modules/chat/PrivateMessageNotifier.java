package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.MessageHeaderS2CPacket;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class PrivateMessageNotifier extends Module {
    public PrivateMessageNotifier() {
        super("private_message_notifier", "Private Message Notifier",
                "Shows a chat alert when a private message is detected. This is a byproduct of the chat signing system.",
                Category.Chat);
    }

    @Override
    public void init() {
        super.init();
        PacketReceiveCallback.EVENT.register(packet -> {
            if (!(packet.get() instanceof MessageHeaderS2CPacket header && enabled)) return;
            PlayerEntity player = Objects.requireNonNull(client.world)
                    .getPlayerByUuid(header.header()
                            .sender());

            if (player == null) return;
            Objects.requireNonNull(client.player)
                    .sendMessage(Text.of(String.format("Σ] `%s` just sent a message.", player.getGameProfile()
                            .getName())));
        });
    }
}
