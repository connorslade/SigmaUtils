package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Packet;

public interface PacketSendCallback {
    Event<PacketSendCallback> EVENT =
            EventFactory.createArrayBacked(PacketSendCallback.class, packetReceiveCallbacks -> packet -> {
                boolean cancel = false;
                for (PacketSendCallback i : packetReceiveCallbacks) cancel |= i.handle(packet);
                return cancel;
            });

    boolean handle(Packet<?> packet);
}
