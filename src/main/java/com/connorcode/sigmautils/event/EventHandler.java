package com.connorcode.sigmautils.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    Priority priority() default Priority.NORMAL;

    enum Priority {
        LOWEST,
        LOW,
        NORMAL,
        HIGH,
        HIGHEST
    }
}
