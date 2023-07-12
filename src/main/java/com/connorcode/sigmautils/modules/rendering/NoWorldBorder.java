package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.event.render.WorldBorderRender;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;

@ModuleInfo(description = "Disables rendering the world border")
public class NoWorldBorder extends Module {
    BoolSetting disableOnShrink = new BoolSetting(NoWorldBorder.class, "Disable on shrink").value(true)
            .description("Will show the world border again if it shrinks. " +
                    "Will reset if you join another world or disable and re-enable.")
            .build();

    boolean shrunk = false;

    @EventHandler
    void onWorldBorderRender(WorldBorderRender event) {
        if (!enabled || (disableOnShrink.value() && shrunk)) return;
        event.cancel();
    }

    @EventHandler
    void onPacketReceive(PacketReceiveEvent packet) {
        if (packet.get() instanceof GameJoinS2CPacket) shrunk = false;
        if (!enabled) return;

        if (packet.get() instanceof WorldBorderCenterChangedS2CPacket ||
                packet.get() instanceof WorldBorderInterpolateSizeS2CPacket ||
                packet.get() instanceof WorldBorderSizeChangedS2CPacket) shrunk = true;
    }

    @Override
    public void disable() {
        super.disable();
        shrunk = false;
    }
}
