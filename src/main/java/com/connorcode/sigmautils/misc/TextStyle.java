package com.connorcode.sigmautils.misc;

import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class TextStyle {
    //        Reset("§r"),
    //        Bold("§l"),
    //        Strikethrough("§m"),
    //        Underline("§n"),
    //        Italic("§o"),
    //        Obfuscated("§k");
    public enum Color {
        Black('0'), DarkBlue('1'), DarkGreen('2'), DarkAqua('3'), DarkRed('4'), DarkPurple('5'), Gold('6'), Gray('7'), DarkGray('8'), Blue('9'), Green('a'), Aqua('b'), Red('c'), LightPurple('d'), Yellow('e'), White('f');

        public final char code;

        Color(char code) {
            this.code = code;
        }

        public String code() {
            return "§" + code;
        }

        public int asHexColor() {
            return Objects.requireNonNull(TextColor.fromFormatting(Formatting.byCode(this.code))).getRgb();
        }
    }
}
