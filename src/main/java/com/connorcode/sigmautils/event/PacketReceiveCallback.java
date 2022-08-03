package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Packet;

public interface PacketReceiveCallback {
    Event<PacketReceiveCallback> EVENT =
            EventFactory.createArrayBacked(PacketReceiveCallback.class, packetReceiveCallbacks -> packet -> {
                boolean cancel = false;
                for (PacketReceiveCallback i : packetReceiveCallbacks) cancel |= i.handle(packet);
                return cancel;
            });

    boolean handle(Packet<?> packet);
}
