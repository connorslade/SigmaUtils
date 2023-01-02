package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.event.UnknownPacketCallback;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import com.connorcode.sigmautils.modules.hud.TimePlayedHud;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.ArrayList;
import java.util.List;

public class BadlionTimers extends HudModule {
    private static final Identifier BADLION_TIMER = new Identifier("badlion", "timers");
    private static final EnumSetting<TimePlayedHud.TimeFormat> timeFormat =
            new EnumSetting<>(BadlionTimers.class, "Time Format", TimePlayedHud.TimeFormat.class).value(
                            TimePlayedHud.TimeFormat.HMS)
                    .description(
                            "The format of the time played. (HMS = Hours:Minutes:Seconds) (BestFit = 5 seconds, 3 hours")
                    .build();
    private static final List<Timer> timers = new ArrayList<>();
    private static long lastTick = 0;

    public BadlionTimers() {
        super("badlion_timers", "Badlion Timers", "Shows badlion timers in the HUD", Category.Server);
        this.defaultOrder = 9;
    }

    @Override
    public void init() {
        super.init();

        PacketReceiveCallback.EVENT.register(packet -> {
            if (packet.get() instanceof GameJoinS2CPacket) timers.clear();
        });

        UnknownPacketCallback.EVENT.register(unknownPacket -> {
            if (!enabled || !unknownPacket.getIdentifier().equals(BADLION_TIMER)) return;
            Pair<Action, JsonObject> packet = decodePacket(unknownPacket.get().getData());

            switch (packet.getLeft()) {
                case REMOVE_ALL_TIMERS, CHANGE_WORLD, REGISTER -> timers.clear();
                case ADD_TIMER -> timers.add(new Timer(packet.getRight()));
                case REMOVE_TIMER -> timers.removeIf(timer -> timer.id == packet.getRight().get("id").getAsInt());
                case SYNC_TIMERS -> timers.stream()
                        .filter(timer -> timer.id == packet.getRight().get("id").getAsInt())
                        .forEach(t -> t.setTicks(packet.getRight().get("time").getAsInt()));
                case UPDATE_TIMER -> timers.replaceAll(timer -> {
                    if (timer.id == packet.getRight().get("id").getAsInt()) return new Timer(packet.getRight());
                    return timer;
                });
            }
            unknownPacket.cancel();
        });
    }

    @Override
    public void tick() {
        super.tick();

        long now = System.currentTimeMillis();
        if (lastTick + 50 > now) return;
        lastTick = now;
        timers.stream().filter(timer -> timer.remainingTicks > 0).forEach(timer -> {
            timer.remainingTicks--;
            if (timer.remainingTicks <= 0 && timer.repeating) timer.remainingTicks = timer.ogTicks;
        });
    }

    @Override
    public List<String> lines() {
        List<String> lines = new ArrayList<>();
        for (Timer timer : timers) {
            long time = timer.remainingTicks * 50;
            String display = switch (timeFormat.value()) {
                case HMS -> DurationFormatUtils.formatDuration(time, "HH:mm:ss");
                case BestFit -> Util.bestTime(time);
            };

            lines.add(String.format("%s%s: §f%s", getTextColor(), timer.name, display));
        }

        return lines;
    }

    private Pair<Action, JsonObject> decodePacket(PacketByteBuf packetByteBuf) {
        byte[] data = new byte[packetByteBuf.readableBytes()];
        for (int i = 0; i < data.length; i++) data[i] = packetByteBuf.readByte();
        String rawString = new String(data);
        String[] parts = rawString.split("\\|", 2);
        return new Pair<>(Action.valueOf(parts[0]), JsonHelper.deserialize(parts[1]));
    }

    enum Action {
        REMOVE_ALL_TIMERS,
        CHANGE_WORLD,
        REGISTER,
        ADD_TIMER,
        REMOVE_TIMER,
        UPDATE_TIMER,
        SYNC_TIMERS
    }

    class Timer {
        int id;
        String name;
        boolean repeating;

        long remainingTicks;
        long ogTicks;

        Timer(JsonObject json) {
            this.id = json.get("id").getAsInt();
            this.name = json.get("name").getAsString();
            this.repeating = json.get("repeating").getAsBoolean();

            this.remainingTicks = json.get("currentTime").getAsLong();
            this.ogTicks = remainingTicks;
        }

        void setTicks(long ticks) {
            this.remainingTicks = ticks;
        }
    }
}

// (badlion:timers) Packet data: [83, 89, 78, 67, 95, 84, 73, 77, 69, 82, 83, 124, 123, 34, 105, 100, 34, 58, 53, 44, 34, 116, 105, 109, 101, 34, 58, 49, 49, 53, 56, 49, 125]
// ADD_TIMER|{"id":9,"name":"Game End","item":{"type":"BEDROCK"},"repeating":false,"time":36000,"millis":-1,"currentTime":36000}

// Events:
// - REMOVE_ALL_TIMERS -- remove all timers
// - CHANGE_WORLD      -- remove all timers
// - REGISTER          -- remove all timers
// - ADD_TIMER         -- add a timer
// - REMOVE_TIMER      -- remove a timer?
// - UPDATE_TIMER      -- update a timer?
// - UPDATE_TIMER_TIME -- idk