package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Stops servers from requiring you to have an resource pack installed.")
public class NoForceResourcePack extends Module {
    @EventHandler
    void onPacketReceive_ResourcePackSendS2CPacket(PacketReceiveEvent packet) {
        if (!(packet.get() instanceof ResourcePackSendS2CPacket resourcePacket) || !enabled) return;
        MutableText text = Text.empty()
                .append(Text.literal(String.format("This server has a %s resource pack.\n",
                                                   resourcePacket.required() ? "required" : "optional"))
                        .formatted(Formatting.BOLD))
                .append(Text.literal("[DOWNLOAD]")
                        .formatted(Formatting.BOLD, Formatting.LIGHT_PURPLE)
                        .styled(style -> style.withClickEvent(
                                        new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                String.format("/util resourcepack server install \"%s\" \"%s\"",
                                                              resourcePacket.url()
                                                                .replaceAll("\"", "\\\""),
                                                              resourcePacket.hash()
                                                                .replaceAll("\"", "\\\""))))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Text.of("Click to install resource pack.")))))
                .append(" ")
                .append(Text.literal("[OPEN LINK]")
                        .formatted(Formatting.BOLD, Formatting.BLUE)
                        .styled(style -> style.withClickEvent(new ClickEvent(
                                        ClickEvent.Action.OPEN_URL, resourcePacket.url()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Text.of("Click to download resource pack.")))));

        Objects.requireNonNull(client.player).sendMessage(text);
        packet.cancel();
    }
}
