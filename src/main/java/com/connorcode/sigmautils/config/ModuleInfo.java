package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.module.Category;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
    String name() default "";

    String id() default "";

    String description();

    Category category() default Category.Unset;
}
