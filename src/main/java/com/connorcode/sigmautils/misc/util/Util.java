package com.connorcode.sigmautils.misc.util;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.misc.AsyncRunner;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class Util {
    static final HashMap<UUID, String> uuidCache = new HashMap<>();
    static final HashSet<UUID> invalidUuids = new HashSet<>();
    static final List<UUID> uuidQueue = new ArrayList<>();

    // == Reflection ==
    public static Object loadNewClass(String classPath) {
        try {
            return SigmaUtils.class.getClassLoader().loadClass(classPath).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String loadResourceString(String name) {
        return new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(SigmaUtils.class.getClassLoader().getResourceAsStream(name)))).lines()
                .collect(Collectors.joining("\n"));
    }

    // == Graphics ==
    public static void addDrawable(Screen screen, Drawable drawable) {
        ScreenAccessor sa = ((ScreenAccessor) screen);
        sa.getDrawables().add(drawable);
        sa.getChildren().add(drawable);
        sa.getSelectables().add(drawable);
    }

    // == String ==
    public static String toSnakeCase(String s) {
        return s.toLowerCase().replace(' ', '_');
    }

    public static String bestTime(long ms) {
        Pair<String, Integer>[] units = new Pair[]{
                new Pair<>("second", 60),
                new Pair<>("minute", 60),
                new Pair<>("hour", 24),
                new Pair<>("day", 30),
                new Pair<>("month", 12),
                new Pair<>("year", 0)
        };
        return bestTime(ms, units, false, 0);
    }

    public static String bestTime(long ms, Pair<String, Integer>[] units, boolean small, int precision) {
        float seconds = ms / 1000f;
        for (Pair<String, Integer> unit : units) {
            if (unit.getRight() == 0 || seconds < unit.getRight()) {
                float intSeconds = precision == 0 ? Math.round(seconds) : seconds;
                return String.format("%." + precision + "f%s%s%s", intSeconds, small ? "" : " ", unit.getLeft(),
                        (intSeconds > 1 && !small) ? "s" : "");
            }

            seconds /= unit.getRight();
        }

        return String.format("%s years", Math.round(seconds));
    }

    // == Minecraft ==
    public static Optional<String> uuidToName(UUID uuid) {
        if (uuidCache.containsKey(uuid)) return Optional.of(uuidCache.get(uuid));
        synchronized (invalidUuids) {
            if (invalidUuids.contains(uuid)) return Optional.empty();
        }
        synchronized (uuidQueue) {
            if (uuidQueue.contains(uuid)) return Optional.empty();
        }

        // Check if the player is in the player list
        var playerList = Objects.requireNonNull(client.getNetworkHandler())
                .getPlayerList()
                .stream()
                .map(PlayerListEntry::getProfile)
                .filter(p -> p != null && p.getId().equals(uuid) && p.getName() != null)
                .findFirst();

        if (playerList.isPresent()) {
            var name = playerList.get().getName();
            synchronized (uuidCache) {
                uuidCache.put(uuid, name);
            }
            return Optional.of(name);
        }

        synchronized (uuidQueue) {
            uuidQueue.add(uuid);
        }
        AsyncRunner.start(new AsyncRunner.Task() {
            @Override
            public String getName() {
                return String.format("UUID lookup (%s)", uuid);
            }

            @Override
            public void start(UUID uuid) {
                var name =
                        client.getSessionService().fillProfileProperties(new GameProfile(uuid, null), false).getName();
                if (name == null) {
                    synchronized (invalidUuids) {
                        invalidUuids.add(uuid);
                    }
                    return;
                }
                synchronized (uuidCache) {
                    uuidCache.put(uuid, name);
                }
            }
        });

        return Optional.empty();
    }

    public enum TimeFormat {
        HMS,
        BestFit;

        public String format(long ms) {
            return switch (this) {
                case HMS -> DurationFormatUtils.formatDuration(ms, "HH:mm:ss");
                case BestFit -> Util.bestTime(ms);
            };
        }
    }
}
