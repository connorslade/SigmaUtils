package com.connorcode.sigmautils.misc.util;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class NetworkUtils {
    static HashMap<String, String> packetNames = new HashMap<>();
    static MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();

    public static Object2IntOpenHashMap<Class<? extends Packet<?>>> getPackets(NetworkSide side) {
        var packets = new Object2IntOpenHashMap<Class<? extends Packet<?>>>();
        Arrays.stream(NetworkState.values())
                .map(state -> state.packetHandlers.get(side))
                .filter(Objects::nonNull)
                .forEach(handler -> packets.putAll(handler.packetIds));
        return packets;
    }

    public static Object2IntOpenHashMap<Class<? extends Packet<?>>> getPackets() {
        var packets = new Object2IntOpenHashMap<Class<? extends Packet<?>>>();
        packets.putAll(getPackets(NetworkSide.CLIENTBOUND));
        packets.putAll(getPackets(NetworkSide.SERVERBOUND));
        return packets;
    }

    public static String getPacketName(Class<? extends Packet<?>> packet) {
        var intermediary = mappingResolver.unmapClassName("intermediary", packet.getName());
        if (packetNames.isEmpty())
            Util.loadResourceString("packets.txt")
                    .lines()
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.split(" "))
                    .forEach(parts -> packetNames.put(parts[0], parts[1]));
        return packetNames.get(intermediary);
    }
}
