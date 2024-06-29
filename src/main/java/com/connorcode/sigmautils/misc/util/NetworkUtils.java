package com.connorcode.sigmautils.misc.util;

import com.connorcode.sigmautils.misc.TextStyle;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.UUID;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class NetworkUtils {
    static HashMap<Identifier, PacketType<? extends Packet<?>>> packetNames = new HashMap<>();
    private static AuthStatus authStatus = AuthStatus.Unknown;
    private static long lastAuthStatusCheck = 0;

    static {
        PlayStateFactories.C2S.forEachPacketType((type, protocolId) -> packetNames.put(type.id(), type));
        PlayStateFactories.S2C.forEachPacketType((type, protocolId) -> packetNames.put(type.id(), type));
    }

    public static PacketType<? extends Packet<?>> getPacket(Identifier id) {
        return packetNames.get(id);
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
