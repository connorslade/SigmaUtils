package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.misc.AsyncRunner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.*;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class Run implements Command {
    static int executeRepeat(CommandContext<FabricClientCommandSource> context, boolean isRepeats) {
        String command = getString(context, "command");
        int delay = getInteger(context, "delay");
        int count = isRepeats ? getInteger(context, "repeats") : -1;
        ClientPlayerEntity player = context.getSource()
                .getPlayer();

        AsyncRunner.start(new AsyncRunner.Task() {
            boolean running = true;
            UUID id;

            @Override
            public String getName() {
                return String.format("Run repeat `%s` (%d)", command, delay);
            }

            @Override
            public boolean running() {
                return running;
            }

            @Override
            public void start(UUID uuid) {
                id = uuid;

                for (int i = 0; true; i++) {
                    if (!running) break;

                    String thisCommand = command.replaceAll("%INDEX%", String.valueOf(i));
                    // TODO: Verify this works
                    if (command.startsWith("/")) player.networkHandler.sendCommand(thisCommand.substring(1));
                    else player.networkHandler.sendChatMessage(thisCommand);
                    if (count != -1 && i >= count) break;

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void stop() {
                running = false;
            }
        });

        return 0;
    }

    static int executeDelay(CommandContext<FabricClientCommandSource> context) {
        String command = getString(context, "command");
        int delay = getInteger(context, "delay");
        ClientPlayerEntity player = context.getSource()
                .getPlayer();

        AsyncRunner.start(new AsyncRunner.Task() {
            UUID id;

            @Override
            public String getName() {
                return String.format("Run delay `%s` (%d)", command, delay);
            }

            @Override
            public boolean running() {
                return true;
            }

            @Override
            public void start(UUID uuid) {
                id = uuid;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (command.startsWith("/")) player.networkHandler.sendCommand(command.substring(1));
                else player.networkHandler.sendChatMessage(command);
            }

            @Override
            public void stop() {
                AsyncRunner.kill(id);
            }
        });

        return 0;
    }

    static int executeFormat(CommandContext<FabricClientCommandSource> context, boolean isCooldown) {
        ClientPlayerEntity player = Objects.requireNonNull(context.getSource()
                .getClient().player);
        String formatString = getString(context, "formatString");
        List<Token> tokens = tokenize(formatString, context.getSource());
        int cooldown = isCooldown ? getInteger(context, "cooldown") : 0;
        if (tokens == null) return 0;

        AsyncRunner.start(new AsyncRunner.Task() {
            boolean running = true;

            @Override
            public String getName() {
                return String.format("Run Formatted `%s`", formatString);
            }

            @Override
            public boolean running() {
                return running;
            }

            @Override
            public void start(UUID uuid) {
                while (running) {
                    Pair<String, Boolean> command = Token.stringify(tokens);
                    String text = command.getLeft();

                    if (text == null) break;
                    if (text.startsWith("/")) player.networkHandler.sendCommand(text.substring(1));
                    else player.networkHandler.sendChatMessage(text);
                    if (command.getRight()) break;

                    try {
                        Thread.sleep(cooldown);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void stop() {
                running = false;
            }
        });

        return 0;
    }

    public static List<Token> tokenize(String string, FabricClientCommandSource sender) {
        List<Token> out = new ArrayList<>();
        char[] chars = string.toCharArray();
        StringBuilder segment = new StringBuilder();
        boolean isInFormat = false;

        for (char chr : chars) {
            if (chr == '%') {
                if (isInFormat) {
                    Token token = Token.fromString(segment.toString());
                    if (token == null) {
                        sender.sendError(Text.of(String.format("Invalid formatter `%s`", segment)));
                        return null;
                    }

                    List<Modifier> modifiers = new ArrayList<>();
                    for (String i : Arrays.stream(segment.toString()
                                    .split(":"))
                            .skip(1)
                            .toList()) {
                        Modifier modifier = Modifier.fromString(i);
                        if (modifier == null) {
                            sender.sendError(Text.of(String.format("Invalid modifier `%s`", i)));
                            return null;
                        }
                        modifiers.add(modifier);
                    }

                    token.modifiers = modifiers;
                    out.add(token);
                    isInFormat = false;
                    segment = new StringBuilder();
                    continue;
                }
                if (segment.length() > 0) out.add(new Token(segment.toString(), Token.TokenType.String));
                segment = new StringBuilder();
                isInFormat = true;
                continue;
            }

            segment.append(chr);
        }

        if (isInFormat) {
            sender.sendError(Text.of("Unclosed Formatter"));
            return null;
        }

        if (segment.length() > 0) out.add(new Token(segment.toString(), Token.TokenType.String));
        return out;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("run")
                        .then(ClientCommandManager.literal("formatted")
                                .then(ClientCommandManager.argument("formatString", string())
                                        .executes(ctx -> executeFormat(ctx, false))
                                        .then(ClientCommandManager.argument("cooldown", integer())
                                                .executes(ctx -> executeFormat(ctx, true)))))
                        .then(ClientCommandManager.literal("delay")
                                .then(ClientCommandManager.argument("command", string())
                                        .then(ClientCommandManager.argument("delay", integer())
                                                .executes(Run::executeDelay))))
                        .then(ClientCommandManager.literal("repeat")
                                .then(ClientCommandManager.argument("command", string())
                                        .then(ClientCommandManager.argument("delay", integer())
                                                .executes(ctx -> executeRepeat(ctx, false))
                                                .then(ClientCommandManager.argument("repeats", integer())
                                                        .executes(ctx -> executeRepeat(ctx, true))))))
                ));
    }

    static class Token {
        public final String data;
        public final TokenType type;
        public List<String> formatterData;
        public List<Modifier> modifiers;
        public int formatterIndex = 0;
        private boolean init = false;

        Token(String data, TokenType type) {
            this.data = data;
            this.type = type;
        }

        public static Token fromString(String str) {
            str = str.split(":")[0];
            if (str.startsWith("{") && str.endsWith("}"))
                return new Token(str.substring(1, str.length() - 1), TokenType.List);

            TokenType type = switch (str.toUpperCase()
                    .replaceAll("_", "")) {
                case "PLAYERNAME" -> TokenType.PlayerName;
                case "PLAYERUUID" -> TokenType.PlayerUUID;
                default -> null;
            };

            if (type == null) return null;
            return new Token(null, type);
        }

        public static Pair<String, Boolean> stringify(List<Token> tokens) {
            StringBuilder out = new StringBuilder();

            boolean incNext = true;
            for (Token i : tokens) {
                if (i.type == TokenType.String) {
                    out.append(i.data);
                    continue;
                }

                Pair<String, Boolean> next = new Pair<>(null, false);
                while (!next.getRight() && next.getLeft() == null) next = i.next(incNext);
                if (next.getLeft() == null) return new Pair<>(null, true);
                incNext = next.getRight();
                out.append(next.getLeft());
            }

            return new Pair<>(out.toString(), incNext);
        }

        public Pair<String, Boolean> next(boolean inc) {
            if (!init) {
                init = true;
                switch (type) {
                    case PlayerName -> {
                        assert client.player != null;
                        if (client.isInSingleplayer()) formatterData = List.of(client.player.getName()
                                .getString());
                        else formatterData = client.player.networkHandler.getPlayerList()
                                .stream()
                                .map(p -> p.getProfile()
                                        .getName())
                                .toList();
                    }
                    case PlayerUUID -> {
                        assert client.player != null;
                        if (client.isInSingleplayer()) formatterData = List.of(client.player.getUuidAsString());
                        else formatterData = client.player.networkHandler.getPlayerList()
                                .stream()
                                .map(p -> p.getProfile()
                                        .getId()
                                        .toString())
                                .toList();
                    }
                    case List -> {
                        char[] chars = data.toCharArray();
                        List<String> out = new ArrayList<>();
                        StringBuilder builder = new StringBuilder();

                        for (int i = 0; i < chars.length; i++) {
                            char chr = chars[i];
                            if (chars[i] == '\\' && i + 1 != chars.length && chars[i + 1] == ',') {
                                builder.append(',');
                                i++;
                                continue;
                            }

                            if (chr == ',') {
                                out.add(builder.toString());
                                builder = new StringBuilder();
                                continue;
                            }
                            builder.append(chr);
                        }

                        if (!builder.isEmpty()) out.add(builder.toString());
                        formatterData = out;
                    }
                }
            }

            String out = Modifier.modify(formatterData.get(formatterIndex), modifiers);
            if (inc) formatterIndex++;
            boolean loop = formatterIndex >= formatterData.size();
            formatterIndex %= formatterData.size();
            return new Pair<>(out, loop);
        }

        enum TokenType {
            String,
            PlayerName,
            PlayerUUID,
            List
        }
    }

    record Modifier(Run.Modifier.Modifiers type) {
        public static Random rand = new Random();

        public static Modifier fromString(String string) {
            if (string.startsWith(":")) string = string.substring(1);

            Modifiers type = switch (string.toUpperCase()
                    .replaceAll("_", "")) {
                case "TITLECASE" -> Modifiers.TitleCase;
                case "LOWERCASE" -> Modifiers.LowerCase;
                case "UPPERCASE" -> Modifiers.UpperCase;
                case "RANDOMCASE" -> Modifiers.RandomCase;
                case "ISONLINE" -> Modifiers.isOnline;
                default -> null;
            };

            if (type == null) return null;
            return new Modifier(type);
        }

        public static String modify(String string, List<Modifier> modifiers) {
            for (Modifier i : modifiers) {
                if (string == null) return null;
                string = switch (i.type) {
                    case TitleCase -> {
                        StringBuilder working = new StringBuilder();
                        char[] chars = string.toCharArray();
                        for (int j = 0; j < chars.length; j++) {
                            if (j > 0 && chars[j - 1] == ' ') working.append(Character.toUpperCase(chars[j]));
                            else working.append(chars[j]);
                        }
                        yield working.toString();
                    }
                    case LowerCase -> string.toLowerCase();
                    case UpperCase -> string.toUpperCase();
                    case RandomCase -> {
                        StringBuilder working = new StringBuilder();
                        for (char j : string.toCharArray()) {
                            if (rand.nextBoolean()) working.append(Character.toUpperCase(j));
                            else working.append(Character.toLowerCase(j));
                        }
                        yield working.toString();
                    }
                    case isOnline -> {
                        Collection<PlayerListEntry> playerListEntries =
                                Objects.requireNonNull(client.player).networkHandler.getPlayerList();
                        String finalString = string;
                        String finalUUIDString = string.replaceAll("-", "");
                        boolean nameMatch = playerListEntries.stream()
                                .anyMatch(p -> Objects.equals(p.getProfile()
                                        .getName()
                                        .toUpperCase(), finalString.toUpperCase()));
                        boolean uuidMatch = playerListEntries.stream()
                                .anyMatch(p -> Objects.equals(p.getProfile()
                                        .getId()
                                        .toString()
                                        .replaceAll("-", ""), finalUUIDString));

                        yield (nameMatch || uuidMatch) ? string : null;
                    }
                };
            }

            return string;
        }

        enum Modifiers {
            // Cases
            TitleCase,
            LowerCase,
            UpperCase,
            RandomCase,

            // Players
            isOnline
        }
    }
}
