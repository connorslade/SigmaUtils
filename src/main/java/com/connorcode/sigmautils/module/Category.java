package com.connorcode.sigmautils.module;

import java.util.Objects;

public enum Category {
    Interface("interface"), Rendering("rendering"), Misc("misc"), Hud("hud"), Chat("chat"), Meta("meta");

    public final String id;

    Category(String id) {
        this.id = id;
    }

    public static Category fromString(String id) {
        for (Category i : Category.values())
            if (Objects.equals(i.id, id)) return i;
        return null;
    }
}
