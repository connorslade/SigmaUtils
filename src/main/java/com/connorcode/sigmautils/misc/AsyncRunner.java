package com.connorcode.sigmautils.misc;

import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AsyncRunner {
    public static final HashMap<UUID, Pair<Task, Thread>> tasks = new HashMap<>();

    public static void start(Task task) {
        UUID uuid = UUID.randomUUID();
        Thread thread = new Thread(() -> {
            task.start(uuid);
            tasks.remove(uuid);
        });
        tasks.put(uuid, new Pair<>(task, thread));
        thread.start();
    }

    public static void stop(UUID uuid) {
        Task task = tasks.get(uuid)
                .getLeft();
        task.stop();
        if (!task.running()) tasks.remove(uuid);
    }

    public static void kill(UUID uuid) {
        tasks.remove(uuid)
                .getRight()
                .interrupt();
    }

    public static List<String> getUuidList() {
        return tasks.keySet()
                .stream()
                .map(UUID::toString)
                .toList();
    }

    public interface Task {
        String getName();

        boolean running();

        void start(UUID uuid);

        void stop();
    }
}
