package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.settings.DummySetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.modules.meta.Padding.getPadding;

@ModuleInfo(description = "Logs every player you see on servers. Kinda cool.",
        documentation = "Currently there is no way to view the player history, other than using NBT Explorer on the SigmaUtils/players.nbt file. In the future I may add an option for it to change the name color or of players you've seen before, which might be cool on big servers.")
public class PlayerHistory extends Module {
    private static final File playerFile = new File(client.runDirectory, "config/SigmaUtils/players.nbt");
    private static final HashMap<String, HashMap<UUID, SeenPlayer>> seenPlayers = new HashMap<>();

    @Override
    public void init() {
        super.init();
        PlayerHistoryDisplay.init();

        try {
            loadPlayers();
        } catch (IOException e) {
            SigmaUtils.logger.error("Failed to load player history file");
            e.printStackTrace();
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            try {
                savePlayers();
            } catch (IOException e) {
                SigmaUtils.logger.error("Failed to save player history file");
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    void onPacketReceive(PacketReceiveEvent packet) {
        if (packet.get() instanceof LoginSuccessS2CPacket) {
            synchronized (seenPlayers) {
                seenPlayers.values().forEach(server -> server.values().forEach(p -> p.setOnline(false)));
            }
            return;
        }

        if (!(packet.get() instanceof PlayerListS2CPacket list) || client.getCurrentServerEntry() == null) return;

        synchronized (seenPlayers) {
            for (PlayerListS2CPacket.Entry entry : list.getPlayerAdditionEntries()) {
                var uuid = entry.profile().getId();
                seenPlayers.putIfAbsent(client.getCurrentServerEntry().address, new HashMap<>());
                var server = seenPlayers.get(client.getCurrentServerEntry().address);

                if (server.containsKey(uuid)) server.get(uuid).update();
                else server.put(uuid, new SeenPlayer(entry.profile().getName(), uuid));
            }
        }
    }

    void loadPlayers() throws IOException {
        if (!playerFile.exists()) return;
        var nbt = NbtIo.readCompressed(playerFile.toPath(), NbtTagSizeTracker.ofUnlimitedBytes());
        synchronized (seenPlayers) {
            for (var server : nbt.getKeys()) {
                var players = new HashMap<UUID, SeenPlayer>();
                for (var player : nbt.getCompound(server).getKeys()) {
                    var seenPlayer = SeenPlayer.deserialize(nbt.getCompound(server).getCompound(player),
                                                            UUID.fromString(player));
                    players.put(seenPlayer.uuid, seenPlayer);
                }
                seenPlayers.put(server, players);
            }
        }
    }

    void savePlayers() throws IOException {
        var nbt = new NbtCompound();
        synchronized (seenPlayers) {
            for (var server : seenPlayers.keySet()) {
                var serverNbt = new NbtCompound();
                for (var player : seenPlayers.get(server).values()) {
                    serverNbt.put(player.uuid.toString(), player.serialize());
                }
                nbt.put(server, serverNbt);
            }
        }
        NbtIo.writeCompressed(nbt, playerFile.toPath());
    }

    static class SeenPlayer {
        private final String name;
        private final UUID uuid;
        private boolean online;
        private int count;

        public SeenPlayer(String name, UUID uuid, int count, boolean online) {
            this.name = name;
            this.uuid = uuid;
            this.count = count;
            this.online = online;
        }

        public SeenPlayer(String name, UUID uuid) {
            this(name, uuid, 1, true);
        }

        static SeenPlayer deserialize(NbtCompound nbt, UUID uuid) {
            return new SeenPlayer(nbt.getString("name"), uuid, nbt.getInt("count"), false);
        }

        public void update() {
            if (!online) this.count++;
            this.online = true;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }

        NbtCompound serialize() {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("name", this.name);
            nbt.putInt("count", this.count);
            return nbt;
        }
    }

    static class PlayerHistoryDisplay {
        static int totalSeen;
        static int thisServer;

        static void init() {
            new DummySetting(PlayerHistory.class, "Total Seen Players", 0) {
                @Override
                public int initRender(Screen screen, int x, int y, int width) {
                    synchronized (seenPlayers) {
                        totalSeen = seenPlayers.values().stream().map(HashMap::size).reduce(0, Integer::sum);
                    }
                    return client.textRenderer.fontHeight + getPadding() * 2;
                }

                @Override
                public void render(RenderData data, int x, int y) {
                    data.drawContext()
                            .drawText(client.textRenderer, "§fTotal Seen Players: " + totalSeen, x, y + getPadding(), 0,
                                    false);
                }
            }.category("Info").build();

            new DummySetting(PlayerHistory.class, "Seen on Current Server", 0) {
                @Override
                public int initRender(Screen screen, int x, int y, int width) {
                    if (client.getCurrentServerEntry() != null) {
                        synchronized (seenPlayers) {
                            thisServer =
                                    seenPlayers.getOrDefault(client.getCurrentServerEntry().address, new HashMap<>())
                                            .size();
                        }
                    }

                    return client.textRenderer.fontHeight + getPadding() * 2;
                }

                @Override
                public void render(RenderData data, int x, int y) {
                    if (client.getCurrentServerEntry() == null) return;
                    data.drawContext().drawText(client.textRenderer, "§fSeen on Current Server: " + thisServer, x,
                            y + getPadding(), 0, false);
                }
            }.category("Info").build();
        }
    }
}