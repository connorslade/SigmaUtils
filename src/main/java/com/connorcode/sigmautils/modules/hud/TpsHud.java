package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.util.ArrayList;
import java.util.List;

public class TpsHud extends HudModule {
    public static final List<Float> tickRateHistory = new ArrayList<>();
    public static long lastTickTime = 0;

    public TpsHud() {
        super("tps_hud", "Tps Hud", "Shows the servers TPS", Category.Hud);
        this.defaultTextColor = TextStyle.Color.Gold;
        this.defaultOrder = 2;
    }

    @Override
    public void init() {
        super.init();

        PacketReceiveCallback.EVENT.register(packet -> {
            if (packet instanceof GameJoinS2CPacket) {
                tickRateHistory.clear();
                lastTickTime = 0;
            }

            if (packet instanceof WorldTimeUpdateS2CPacket) {
                long now = System.currentTimeMillis();
                if (lastTickTime != 0) tickRateHistory.add(20f / ((float) (now - lastTickTime) / 1000f));
                lastTickTime = now;

                while (tickRateHistory.size() > 20) tickRateHistory.remove(0);
            }

            return false;
        });
    }

    public String line() {
        float avg = 0;
        for (float i : tickRateHistory) avg += i;

        return String.format("§r%sTPS: §f%.1f", this.getTextColor(), avg / tickRateHistory.size());
    }
}
