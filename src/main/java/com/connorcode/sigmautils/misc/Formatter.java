package com.connorcode.sigmautils.misc;

import com.mojang.logging.LogUtils;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Formatter {
    private static final HashMap<String, List<Token>> cache = new HashMap<>();
    private final HashMap<String, Object> args = new HashMap<>();
    private final List<Token> tokens;
    private List<Object> posArgs = new ArrayList<>();

    public Formatter(String formatString) {
        this.tokens = new Formatter(formatString, true).tokens;
    }

    public Formatter(String formatString, Object... args) {
        this.tokens = new Formatter(formatString, true).tokens;
        this.posArgs = Arrays.stream(args)
                .toList();
    }

    public Formatter(String formatString, boolean cache) {
        if (Formatter.cache.containsKey(formatString)) {
            this.tokens = Formatter.cache.get(formatString);
            return;
        }

        this.tokens = Token.tokenize(formatString);
        if (cache)
            Formatter.cache.put(formatString, tokens);
    }

    public Formatter arg(String name, Object value) {
        args.put(name, value);
        return this;
    }

    public Formatter arg(Object value) {
        posArgs.add(value);
        return this;
    }

    public String format() {
        StringBuilder out = new StringBuilder();

        for (Token i : tokens) {
            switch (i.type) {
                case String -> out.append(i.data);
                case PosFormat -> {
                    if (posArgs.isEmpty())
                        throw new RuntimeException("Not enough positional arguments defined!");
                    out.append(posArgs.remove(0));
                }
                case Format -> {
                    if (!args.containsKey(i.data))
                        throw new RuntimeException(new Formatter("The key `{}` is not defined!").arg(i.data)
                                .format());
                    out.append(args.remove(i.data));
                }
            }
        }

        if (!posArgs.isEmpty() || !args.isEmpty())
            LogUtils.getLogger()
                    .warn("Formatter arguments not all used!");
        return out.toString();
    }

    record Token(Formatter.Token.TokenType type, String data) {
        static List<Token> tokenize(String formatString) {
            List<Token> tokens = new ArrayList<>();
            char[] chars = formatString.toCharArray();
            StringBuilder builder = new StringBuilder();
            boolean formatterOpen = false;

            for (int i = 0; i < chars.length; i++) {
                char chr = chars[i];

                if (chr == '{') {
                    Pair<Integer, Integer> cc = countChars('{', i, chars);
                    if (cc.getLeft() % 2 == 0) {
                        builder.append('{');
                        i = cc.getRight();
                        continue;
                    }

                    if (formatterOpen)
                        throw new RuntimeException("Opening formatter in formatter!?");

                    if (!builder.isEmpty()) {
                        tokens.add(new Token(TokenType.String, builder.toString()));
                        builder = new StringBuilder();
                    }

                    formatterOpen = true;
                    continue;
                }

                if (chr == '}') {
                    Pair<Integer, Integer> cc = countChars('}', i, chars);
                    if (cc.getLeft() % 2 == 0) {
                        builder.append('}');
                        i = cc.getRight();
                        continue;
                    }

                    if (formatterOpen) {
                        formatterOpen = false;
                        String build = builder.toString();
                        builder = new StringBuilder();
                        tokens.add(new Token(build.isEmpty() ? TokenType.PosFormat : TokenType.Format, build));
                        continue;
                    }

                    throw new RuntimeException("Closing formatter without open!");
                }

                builder.append(chr);
            }

            if (formatterOpen)
                throw new RuntimeException("Unclosed Formatter");
            if (!builder.isEmpty())
                tokens.add(new Token(TokenType.String, builder.toString()));

            return tokens;
        }

        private static Pair<Integer, Integer> countChars(char chr, int index, char[] data) {
            int count = 0;
            index--;

            while (++index < data.length && data[index] == chr)
                count++;

            return new Pair<>(count, index - 1);
        }

        enum TokenType {
            String, Format, PosFormat
        }
    }
}
