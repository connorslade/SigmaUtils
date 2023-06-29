package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.network.PacketSendCallback;
import com.connorcode.sigmautils.mixin.CustomPayloadC2SPacketAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

public class BrandChanger extends Module {
    static StringSetting brand = new StringSetting(BrandChanger.class, "brand")
            .value("vanilla")
            .build();

    public BrandChanger() {
        super("brand_changer", "Brand Changer", "Lets you change the brand reported to servers", Category.Misc);
    }

    @Override
    public void init() {
        super.init();

        PacketSendCallback.EVENT.register(packet -> {
            if (!(packet.get() instanceof CustomPayloadC2SPacket) || !enabled) return;
            CustomPayloadC2SPacketAccessor payloadPacket = (CustomPayloadC2SPacketAccessor) packet.get();
            payloadPacket.setData(new PacketByteBuf(Unpooled.buffer()).writeString(brand.value()));
        });
    }
}