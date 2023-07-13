package com.connorcode.sigmautils.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentedEnum {
    String value();
}
