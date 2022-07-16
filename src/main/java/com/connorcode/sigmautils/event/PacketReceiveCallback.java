package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Packet;

public interface PacketReceiveCallback {
    Event<PacketReceiveCallback> EVENT =
            EventFactory.createArrayBacked(PacketReceiveCallback.class, packetReceiveCallbacks -> packet -> {
                for (PacketReceiveCallback i : packetReceiveCallbacks) if (i.handle(packet)) return true;
                return false;
            });

    boolean handle(Packet<?> packet);
}
