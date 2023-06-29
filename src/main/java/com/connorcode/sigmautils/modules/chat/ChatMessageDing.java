package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.event.network.PacketReceiveCallback;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.sound.SoundEvents;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class ChatMessageDing extends Module {
    private static final BoolSetting alertSystemMessages =
            new BoolSetting(ChatMessageDing.class, "System Messages").value(true)
                    .description("Play ding for system messages")
                    .build();
    private static final BoolSetting alertActionBar = new BoolSetting(ChatMessageDing.class, "Action Bar").value(false)
            .description("Play ding for action bar messages")
            .build();

    // TODO: Self messages

    public ChatMessageDing() {
        super("chat_message_ding", "Chat Message Ding", "Plays a sound when you receive a chat message", Category.Chat);
    }

    private void playDing() {
        client
                .getSoundManager()
                .play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f));
    }

    @Override
    public void init() {
        super.init();

        PacketReceiveCallback.EVENT.register(packet -> {
            if (!enabled) return;
            if (packet.get() instanceof ChatMessageS2CPacket) playDing();
            if (packet.get() instanceof GameMessageS2CPacket gameMessageS2CPacket && alertSystemMessages.value()) {
                if (gameMessageS2CPacket.overlay() && !alertActionBar.value()) return;
                playDing();
            }
        });
    }
}
