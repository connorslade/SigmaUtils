package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

import java.util.Objects;

public class TimePlayedHud extends HudModule {
    private static final long openTimestamp = System.currentTimeMillis();
    private static final EnumSetting<TimeSince> timeSince =
            new EnumSetting<>(TimePlayedHud.class, "Time Since", TimeSince.class).value(TimeSince.GameStart)
                    .description("The time since when the time played should be calculated")
                    .build();
    private static final EnumSetting<Util.TimeFormat> timeFormat =
            new EnumSetting<>(TimePlayedHud.class, "Time Format", Util.TimeFormat.class).value(Util.TimeFormat.HMS)
                    .description(
                            "The format of the time played. (HMS = Hours:Minutes:Seconds) (BestFit = 5 seconds, 3 hours")
                    .build();
    private static long worldOpenTimestamp = System.currentTimeMillis();

    public TimePlayedHud() {
        super("time_played_hud", "Time Played Hud", "Shows how long your client has been open for this session",
                Category.Hud);
        this.defaultTextColor = TextStyle.Color.Blue;
        this.defaultOrder = 8;
    }

    @Override
    public void init() {
        super.init();

        PacketReceiveCallback.EVENT.register(packet -> {
            if (packet.get() instanceof GameJoinS2CPacket) worldOpenTimestamp = System.currentTimeMillis();
        });
    }

    public String line() {
        long time = switch (timeSince.value()) {
            case GameStart -> System.currentTimeMillis() - openTimestamp;
            case WorldLoad -> System.currentTimeMillis() - worldOpenTimestamp;
            case WorldCreate -> Objects.requireNonNull(MinecraftClient.getInstance().world)
                    .getTime() * 50;
        };

        return String.format("§r%sPlayed: §f%s", this.getTextColor(), timeFormat.value().format(time));
    }

    public enum TimeSince {
        GameStart,
        WorldLoad,
        WorldCreate
    }
}
