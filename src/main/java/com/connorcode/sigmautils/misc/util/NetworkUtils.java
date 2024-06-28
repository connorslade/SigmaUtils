package com.connorcode.sigmautils.misc.util;

import com.connorcode.sigmautils.misc.TextStyle;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class NetworkUtils {
    static HashMap<String, String> packetNames = new HashMap<>();
    static MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
    private static AuthStatus authStatus = AuthStatus.Unknown;
    private static long lastAuthStatusCheck = 0;

    public static Object2IntOpenHashMap<Class<? extends Packet<?>>> getPackets(NetworkSide side) {
        var packets = new Object2IntOpenHashMap<Class<? extends Packet<?>>>();
        // TODO: Fix
//        Arrays.stream(NetworkPhase.values())
//                .map(state -> state.packetHandlers.get(side))
//                .filter(Objects::nonNull)
//                .forEach(handler -> packets.putAll(handler.backingHandler.packetIds));
        return packets;
    }

    public static Object2IntOpenHashMap<Class<? extends Packet<?>>> getPackets() {
        var packets = new Object2IntOpenHashMap<Class<? extends Packet<?>>>();
        packets.putAll(getPackets(NetworkSide.CLIENTBOUND));
        packets.putAll(getPackets(NetworkSide.SERVERBOUND));
        return packets;
    }

    public static String getPacketName(Class<? extends Packet<?>> packet) {
        return getPacketName(packet.getName());
    }

    @Nullable
    public static String getPacketName(String bytecodeName) {
        var intermediary = mappingResolver.unmapClassName("intermediary", bytecodeName);
        if (packetNames.isEmpty())
            Util.loadResourceString("assets/sigma-utils/packets.txt")
                    .lines()
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.split(" "))
                    .forEach(parts -> packetNames.put(parts[0], parts[1]));
        return packetNames.get(intermediary);
    }

    public static synchronized AuthStatus getAuthStatus() {
        if (authStatus == AuthStatus.Waiting) return authStatus;
        if (System.currentTimeMillis() - lastAuthStatusCheck <= 1000 * 60 * 5) return authStatus;

        authStatus = AuthStatus.Waiting;
        lastAuthStatusCheck = System.currentTimeMillis();

        // TODO: Verify that this works
        new Thread(() -> {
            var session = client.getSession();
            var token = session.getAccessToken();
            var id = UUID.randomUUID().toString();

            // Thank you https://github.com/axieum/authme
            var sessionService = (YggdrasilMinecraftSessionService) client.getSessionService();
            try {
                sessionService.joinServer(client.getSession().getUuidOrNull(), token, id);
                authStatus = sessionService.hasJoinedServer(session.getUsername(), id, null) != null ? AuthStatus.Online : AuthStatus.Offline;
            } catch (AuthenticationException e) {
                authStatus = AuthStatus.Invalid;
            }
        }).start();

        return authStatus;
    }

    public enum AuthStatus {
        Unknown,
        Waiting,
        Invalid,
        Online,
        Offline;

        public String getText() {
            return switch (this) {
                case Unknown, Waiting -> "Waiting";
                case Invalid -> "Invalid";
                case Online -> TextStyle.Color.Green.code() + "Online";
                case Offline -> TextStyle.Color.Red.code() + "Offline";
            };
        }
    }
}
