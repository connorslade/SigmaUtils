package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TpsHud extends HudModule {
    public static final List<Float> tickRateHistory = new ArrayList<>();
    private static final NumberSetting samples = new NumberSetting(TpsHud.class, "Samples", 10, 40).value(20)
            .description("The amount of samples to take to average the TPS")
            .precision(0)
            .build();
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
            if (packet.get() instanceof GameJoinS2CPacket) {
                tickRateHistory.clear();
                lastTickTime = 0;
                return;
            }

            if (packet.get() instanceof WorldTimeUpdateS2CPacket) {
                long now = System.currentTimeMillis();
                if (lastTickTime != 0) tickRateHistory.add(20f / ((float) (now - lastTickTime) / 1000f));
                lastTickTime = now;

                while (tickRateHistory.size() > samples.intValue()) tickRateHistory.remove(0);
            }
        });
    }

    public String line() {
        float avg = tickRateHistory.stream().filter(Objects::nonNull).reduce(Float::sum).orElse(0f) /
                tickRateHistory.size();
        return String.format("§r%sTPS: §f%.1f", this.getTextColor(), avg);
    }
}
