package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class NoForceResourcePack extends Module {
    public NoForceResourcePack() {
        super("no_force_resourcepack", "No Force ResourcePack",
                "Stops servers from requiring you to have an resource pack installed.", Category.Misc);
    }

    @Override
    public void init() {
        super.init();
        PacketReceiveCallback.EVENT.register(packet -> {
            if (!(packet instanceof ResourcePackSendS2CPacket resourcePacket) || !enabled) return false;
            MutableText text = Text.empty()
                    .append(Text.literal(String.format("This server has a %s resource pack.\n",
                                    resourcePacket.isRequired() ? "required" : "optional"))
                            .formatted(Formatting.BOLD))
                    .append(Text.literal("[DOWNLOAD]")
                            .formatted(Formatting.BOLD, Formatting.LIGHT_PURPLE)
                            .styled(style -> style.withClickEvent(
                                            new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                    String.format("/util resourcepack server install \"%s\" \"%s\"",
                                                            resourcePacket.getURL()
                                                                    .replaceAll("\"", "\\\""),
                                                            resourcePacket.getSHA1()
                                                                    .replaceAll("\"", "\\\""))))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            Text.of("Click to install resource pack.")))))
                    .append(" ")
                    .append(Text.literal("[OPEN LINK]")
                            .formatted(Formatting.BOLD, Formatting.BLUE)
                            .styled(style -> style.withClickEvent(new ClickEvent(
                                            ClickEvent.Action.OPEN_URL, resourcePacket.getURL()))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            Text.of("Click to download resource pack.")))));

            Objects.requireNonNull(MinecraftClient.getInstance().player)
                    .sendMessage(text);
            return true;
        });
    }
}
