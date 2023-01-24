package com.connorcode.sigmautils.misc.util;

import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.mixin.YggdrasilAuthenticationServiceAccessor;
import com.connorcode.sigmautils.mixin.YggdrasilUserAuthenticationAccessor;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class NetworkUtils {
    static HashMap<String, String> packetNames = new HashMap<>();
    static MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
    private static AuthStatus authStatus = AuthStatus.Unknown;
    private static long lastAuthStatusCheck = 0;

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

    public static synchronized AuthStatus getAuthStatus() {
        if (authStatus == AuthStatus.Waiting) return authStatus;

        if (System.currentTimeMillis() - lastAuthStatusCheck > 1000 * 60 * 5) {
            authStatus = AuthStatus.Waiting;
            lastAuthStatusCheck = System.currentTimeMillis();
            new Thread(() -> {
                if (!(client.getSessionService() instanceof YggdrasilMinecraftSessionService sessionService)) return;
                var authService = sessionService.getAuthenticationService();
                if (((YggdrasilAuthenticationServiceAccessor) authService).getClientToken() == null) {
                    authStatus = AuthStatus.Invalid;
                    return;
                }
                var auth = authService.createUserAuthentication(Agent.MINECRAFT);
                if (!(auth instanceof YggdrasilUserAuthentication userAuth)) return;
                authStatus =
                        ((YggdrasilUserAuthenticationAccessor) userAuth).invokeCheckTokenValidity() ? AuthStatus.Online : AuthStatus.Offline;
            }).start();
        }

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
