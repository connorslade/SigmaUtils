package com.connorcode.sigmautils.event;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    final HashMap<Class<? extends Event>, List<Handler>> listeners = new HashMap<>();
    boolean finalized;

    public EventBus() {}

    /**
     * Registers all methods in a class with the {@link EventHandler} annotation
     *
     * @param _class The class to register
     */
    public void register(Object _class) {
        for (var i : _class.getClass().getDeclaredMethods()) {
            if (!i.isAnnotationPresent(EventHandler.class)) continue;
            var annotation = i.getAnnotation(EventHandler.class);
            i.setAccessible(true);

            if (i.getParameterCount() != 1)
                throw new RuntimeException(
                        String.format("Method %s has %d parameters, expected 1", i.getName(), i.getParameterCount()));
            var event = (Class<? extends Event>) i.getParameterTypes()[0];

            Consumer<Object> consumer = (arg) -> {
                try {
                    i.invoke(_class, arg);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
            var handler = new Handler(consumer, annotation.priority());

            synchronized (listeners) {
                if (listeners.containsKey(event))
                    listeners.get(event).add(handler);
                else {
                    listeners.put(event, new ArrayList<>(List.of(handler)));
                    if (finalized) listeners.get(event).add(handler);
                }
            }
        }
    }

    public void _finalize() {
        synchronized (listeners) {
            for (var i : listeners.values())
                i.sort((a, b) -> b.priority.compareTo(a.priority));
        }
        finalized = true;
    }

    public <T extends Event> void post(T event) {
        synchronized (listeners) {
            var listeners = this.listeners.get(event.getClass());
            if (listeners != null) listeners.forEach(handler -> handler.handler.accept(event));
        }
    }

    static class Handler {
        Consumer<Object> handler;
        EventHandler.Priority priority;

        public Handler(Consumer<Object> handler, EventHandler.Priority priority) {
            this.handler = handler;
            this.priority = priority;
        }
    }
}
