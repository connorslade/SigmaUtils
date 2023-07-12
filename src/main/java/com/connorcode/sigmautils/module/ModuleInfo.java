package com.connorcode.sigmautils.module;

import com.connorcode.sigmautils.module.Category;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
    String name() default "";

    String id() default "";

    Category category() default Category.Unset;

    String description();

    String documentation() default "";
}
