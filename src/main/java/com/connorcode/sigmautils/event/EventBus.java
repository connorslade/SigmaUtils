package com.connorcode.sigmautils.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventBus {
    HashMap<Class<? extends EventI>, List<Handler>> listeners = new HashMap<>();

    public EventBus() {}

    public void register(Class<?> _class) {
        for (var i : _class.getDeclaredMethods()) {
            if (!i.isAnnotationPresent(EventHandler.class)) continue;
            var annotation = i.getAnnotation(EventHandler.class);
            var handler = new Handler(i, annotation.priority());
            System.out.println("Registered handler for " + annotation.value().getSimpleName() + " with priority " +
                    annotation.priority());

            if (listeners.containsKey(annotation.value()))
                listeners.get(annotation.value()).add(handler);
            else listeners.put(annotation.value(), new ArrayList<>(List.of(handler)));
        }
    }

    public void _finalize() {
        for (var i : listeners.values())
            i.sort((a, b) -> b.priority.compareTo(a.priority));
    }

    static class Handler {
        Method handler;
        EventHandler.Priority priority;

        public Handler(Method handler, EventHandler.Priority priority) {
            this.handler = handler;
            this.priority = priority;
        }
    }
}
