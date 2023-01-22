package com.connorcode.sigmautils.misc.util;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;

import java.util.Arrays;
import java.util.Objects;

public class NetworkUtils {
    public static Object2IntOpenHashMap<Class<? extends Packet<?>>> getPackets(NetworkSide side) {
        var packets = new Object2IntOpenHashMap<Class<? extends Packet<?>>>();
        Arrays.stream(NetworkState.values())
                .map(state -> state.packetHandlers.get(side))
                .filter(Objects::nonNull)
                .forEach(handler -> packets.putAll(handler.packetIds));
        return packets;
    }
}
