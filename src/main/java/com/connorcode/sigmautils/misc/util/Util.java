package com.connorcode.sigmautils.misc.util;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.misc.AsyncRunner;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class Util {
    static final HashMap<UUID, String> uuidCache = new HashMap<>();
    static final HashSet<UUID> invalidUuids = new HashSet<>();
    static final List<UUID> uuidQueue = new ArrayList<>();

    // == Reflection ==
    public static Object loadNewClass(String classPath) {
        try {
            var constructor = SigmaUtils.class.getClassLoader().loadClass(classPath).getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String loadResourceString(String name) {
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(SigmaUtils.class.getClassLoader()
                .getResourceAsStream(name)))).lines().collect(Collectors.joining("\n"));
    }

    public static <T> Class<T> classFromGeneric() {
        var list = new ArrayList<T>(0);
        var type = ((ParameterizedType) list.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return (Class<T>) type.getClass();
    }

    // == Graphics ==
    public static void addDrawable(Screen screen, Drawable drawable) {
        ((ScreenAccessor) screen).getDrawables().add(drawable);
    }

    public static void addChild(Screen screen, Drawable drawable) {
        ScreenAccessor sa = ((ScreenAccessor) screen);
        sa.getDrawables().add(drawable);
        sa.getChildren().add(drawable);
        sa.getSelectables().add(drawable);
    }

    public static <T, K> K nullMap(T t, Function<T, K> mapper) {
        return t == null ? null : mapper.apply(t);
    }

    // == String ==
    public static String toSnakeCase(String s) {
        return s.toLowerCase().replace(' ', '_');
    }

    public static String titleString(String s) {
        StringBuilder out = new StringBuilder();

        for (var i : s.toCharArray()) {
            if (Character.isUpperCase(i) && !out.isEmpty()) out.append(" ");
            out.append(i);
        }

        return out.toString();
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
        if (uuidCache.containsKey(uuid))
            return Optional.of(uuidCache.get(uuid));
        synchronized (invalidUuids) {
            if (invalidUuids.contains(uuid))
                return Optional.empty();
        }
        synchronized (uuidQueue) {
            if (uuidQueue.contains(uuid))
                return Optional.empty();
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
                var name = client.getSessionService()
                        .fillProfileProperties(new GameProfile(uuid, null), false)
                        .getName();
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

    public static <T extends Module> EnumSetting<FilterType> filterSetting(Class<T> _class) {
        return new EnumSetting<>(_class, "Filter Type", Util.FilterType.class).value(FilterType.Blacklist)
                .description("Whether to blacklist or whitelist entities.");
    }

    public enum TimeFormat implements DocumentedEnum {
        HMS,
        BestFit;

        public String format(long ms) {
            return switch (this) {
                case HMS -> DurationFormatUtils.formatDuration(ms, "HH:mm:ss");
                case BestFit -> Util.bestTime(ms);
            };
        }

        @Override
        public String[] getDocs() {
            return new String[]{
                    "Hours, minutes, seconds (HH:mm:ss)",
                    "Best time format (e.g. 1 minute, 2 days)"
            };
        }
    }

    public enum FilterType implements DocumentedEnum {
        Blacklist,
        Whitelist;

        @Override
        public String[] getDocs() {
            return new String[]{
                    "Only elements in the list will be targeted.",
                    "All elements except those in the list will be targeted."
            };
        }
    }

    public interface DocumentedEnum {
        String[] getDocs();
    }
}
