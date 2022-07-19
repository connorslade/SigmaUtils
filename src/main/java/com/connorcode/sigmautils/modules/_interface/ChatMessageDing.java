package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.sound.SoundEvents;

public class ChatMessageDing extends BasicModule {
    public ChatMessageDing() {
        super("chat_message_ding", "Chat Message Ding", "Plays a sound when you receive a chat message",
                Category.Interface);
    }

    @Override
    public void init() {
        super.init();

        PacketReceiveCallback.EVENT.register(packet -> {
            if (enabled && (packet instanceof ChatMessageS2CPacket || packet instanceof GameMessageS2CPacket))
                MinecraftClient.getInstance()
                        .getSoundManager()
                        .play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f));
            return false;
        });
    }
}
