package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.network.PacketSendEvent;
import com.connorcode.sigmautils.mixin.CustomPayloadC2SPacketAccessor;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

@ModuleInfo(description = "Lets you change the brand reported to servers")
public class BrandChanger extends Module {
    static StringSetting brand = new StringSetting(BrandChanger.class, "brand")
            .value("vanilla")
            .build();

    @EventHandler
    void onPacketSend(PacketSendEvent packet) {
        if (!(packet.get() instanceof CustomPayloadC2SPacket) || !enabled) return;
        CustomPayloadC2SPacketAccessor payloadPacket = (CustomPayloadC2SPacketAccessor) packet.get();
        payloadPacket.setData(new PacketByteBuf(Unpooled.buffer()).writeString(brand.value()));
    }
}