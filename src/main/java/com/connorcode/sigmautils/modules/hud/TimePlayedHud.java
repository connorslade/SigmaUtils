package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class TimePlayedHud extends HudModule {
    private static final long openTimestamp = System.currentTimeMillis();
    private static final EnumSetting<TimeSince> timeSince = new EnumSetting<>(TimePlayedHud.class, "Time Since", TimeSince.class).value(TimeSince.GameStart)
            .description("The time since when the time played should be calculated")
            .build();

    public TimePlayedHud() {
        super("time_played_hud", "Time Played Hud", "Shows how long your client has been open for this session", Category.Hud);
        this.defaultTextColor = TextStyle.Color.Blue;
        this.defaultOrder = 8;
    }

    public String line() {
        long time = switch (timeSince.value()) {
            case GameStart -> openTimestamp;
            case WorldLoad -> throw new RuntimeException("Not implemented yet");
        };
        return String.format("§r%sPlayed: §f%s", this.getTextColor(), DurationFormatUtils.formatDuration(time, "HH:mm:ss"));
    }

    public enum TimeSince {
        GameStart,
        WorldLoad,
    }
}
