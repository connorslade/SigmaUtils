package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.network.PacketSendEvent;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Lets you change the brand reported to servers")
public class BrandChanger extends Module {
    static StringSetting brand = new StringSetting(BrandChanger.class, "brand").value("vanilla").build();

    @EventHandler
    void onPacketSend(PacketSendEvent event) {
        if (client.getNetworkHandler() == null || !enabled || !(event.get() instanceof CustomPayloadC2SPacket packet) ||
            !(packet.payload() instanceof BrandCustomPayload))
            return;

        var newPacket = new CustomPayloadC2SPacket(new BrandCustomPayload(brand.value()));
        client.getNetworkHandler().sendPacket(newPacket);
        event.cancel();
    }
}