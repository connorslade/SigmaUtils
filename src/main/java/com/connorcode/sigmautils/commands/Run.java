package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.misc.AsyncRunner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
                    if (!running || (count != -1 && i >= count)) break;

                    String thisCommand = command.replaceAll("%INDEX%", String.valueOf(i));
                    if (command.startsWith("/")) player.sendCommand(thisCommand.substring(1), null);
                    else player.sendChatMessage(thisCommand, null);

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

                if (command.startsWith("/")) player.sendCommand(command.substring(1), null);
                else player.sendChatMessage(command, null);
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
        System.out.println(tokens);

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
                    System.out.println(text);

                    if (text.startsWith("/")) player.sendCommand(text.substring(1), null);
                    else player.sendChatMessage(text, null);
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
        public int formatterIndex = 0;
        private boolean init = false;

        Token(String data, TokenType type) {
            this.data = data;
            this.type = type;
        }

        public static Token fromString(String str) {
            TokenType type = switch (str) {
                case "PLAYER_NAME" -> TokenType.PlayerName;
                case "PLAYER_UUID" -> TokenType.PlayerUUID;
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

                Pair<String, Boolean> next = i.next(incNext);
                incNext = next.getRight();
                out.append(next.getLeft());
            }

            return new Pair<>(out.toString(), incNext);
        }

        public Pair<String, Boolean> next(boolean inc) {
            if (!init) {
                init = true;
                MinecraftClient client = MinecraftClient.getInstance();
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
                    default -> throw new RuntimeException();
                }
                System.out.println(formatterData);
            }

            String out = formatterData.get(formatterIndex);
            if (inc) formatterIndex++;
            boolean loop = formatterIndex >= formatterData.size();
            formatterIndex %= formatterData.size();
            return new Pair<>(out, loop);
        }

        enum TokenType {
            String, PlayerName, PlayerUUID
        }
    }
}
