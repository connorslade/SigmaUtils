package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.list.PacketList;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.event.network.PacketSendEvent;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.SigmaUtils.client;

// Warning: When updating versions the packets may change and this won't be automatically updated, causing unexpected behaviour (gasp)
@ModuleInfo(description = "Cancels the specified packets.")
public class PacketCanceler extends Module {
    DynamicListSetting<PacketType<? extends Packet<?>>> clientPackets =
            new DynamicListSetting<>(PacketCanceler.class, "Client Packets",
                    PacketCanceler::getClientPacketManager).description("Packets to the client.").build();
    DynamicListSetting<PacketType<? extends Packet<?>>> serverPackets =
            new DynamicListSetting<>(PacketCanceler.class, "Server Packets",
                    PacketCanceler::getServerPacketManager).description("Packets to the server.").build();
    BoolSetting debug = new BoolSetting(PacketCanceler.class, "Debug")
            .description("Prints debug messages when a packet is canceled.")
            .build();

    static DynamicListSetting.ResourceManager<PacketType<? extends Packet<?>>> getClientPacketManager(DynamicListSetting<PacketType<? extends Packet<?>>> setting) {
        return new PacketList(setting, NetworkSide.CLIENTBOUND);
    }

    static DynamicListSetting.ResourceManager<PacketType<? extends Packet<?>>> getServerPacketManager(DynamicListSetting<PacketType<? extends Packet<?>>> setting) {
        return new PacketList(setting, NetworkSide.SERVERBOUND);
    }

    @EventHandler
    void onPacketSend(PacketSendEvent packet) {
        if (!enabled || !serverPackets.value().contains(packet.packet.getPacketId())) return;
        sendDebug(NetworkSide.SERVERBOUND, packet.get());
        packet.cancel();
    }

    @EventHandler
    void onPacketReceive(PacketReceiveEvent packet) {
        if (!enabled || !clientPackets.value().contains(packet.packet.getPacketId())) return;
        sendDebug(NetworkSide.CLIENTBOUND, packet.get());
        packet.cancel();
    }

    void sendDebug(NetworkSide side, Packet<?> packet) {
        if (client.player == null || !debug.value()) return;
        client.player.sendMessage(Text.of(String.format("[SIGMAUTILS::PacketCancel] Cancelling %s packet %s", side, packet.getPacketId()
                .id().getPath())), false);
    }
}
