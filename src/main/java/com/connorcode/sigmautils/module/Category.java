package com.connorcode.sigmautils.module;

public enum Category {
    Interface("interface"),
    Rendering("rendering"),
    Misc("misc"),
    Hud("hud"),
    Chat("chat"),
    Meta("meta"),
    Server("server");

    public final String id;

    Category(String id) {
        this.id = id;
    }
}
