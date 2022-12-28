package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.PacketSendCallback;
import com.connorcode.sigmautils.mixin.CustomPayloadC2SPacketAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

public class FakeVanilla extends Module {
    static StringSetting brand = new StringSetting(FakeVanilla.class, "brand")
            .value("vanilla")
            .build();

    public FakeVanilla() {
        super("fake_vanilla", "Fake Vanilla", "Reports to be a vanilla client when joining servers", Category.Misc);
    }

    @Override
    public void init() {
        super.init();

        PacketSendCallback.EVENT.register(packet -> {
            if (!(packet instanceof CustomPayloadC2SPacket) || !enabled) return false;
            CustomPayloadC2SPacketAccessor payloadPacket = (CustomPayloadC2SPacketAccessor) packet;
            payloadPacket.setData(new PacketByteBuf(Unpooled.buffer()).writeString(brand.value()));
            return false;
        });
    }
}
