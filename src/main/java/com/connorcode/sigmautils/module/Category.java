package com.connorcode.sigmautils.module;


public enum Category {
    Interface("interface"),
    Rendering("rendering"),
    Misc("misc"),
    Hud("hud"),
    Chat("chat"),
    Meta("meta"),
    Server("server"),
    Unset("unset");

    public final String id;

    Category(String id) {
        this.id = id;
    }

    static Category guessCategory(Class<?> _class) {
        var path = _class.getPackage().getName().split("\\.");
        var name = path[path.length - 1];
        return switch (name) {
            case "_interface" -> Interface;
            case "rendering" -> Rendering;
            case "misc" -> Misc;
            case "hud" -> Hud;
            case "chat" -> Chat;
            case "meta" -> Meta;
            case "server" -> Server;
            default -> Unset;
        };
    }

    public static Category[] realValues() {
        return new Category[]{
                Interface,
                Rendering,
                Misc,
                Hud,
                Chat,
                Meta,
                Server
        };
    }

    Category assertValid() {
        if (this == Unset) throw new RuntimeException("Category is unset");
        return this;
    }
}
