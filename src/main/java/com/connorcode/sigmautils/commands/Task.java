package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.misc.AsyncRunner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.CommandSource.suggestMatching;

public class Task implements Command {
    private static int onList(CommandContext<FabricClientCommandSource> context) {
        List<Pair<UUID, AsyncRunner.Task>> tasks = AsyncRunner.tasks.entrySet()
                .stream()
                .map(e -> new Pair<>(e.getKey(), e.getValue()
                        .getLeft()))
                .toList();
        MutableText message = Text.empty();

        if (tasks.isEmpty()) message.append("No tasks are running");
        for (int index = 0; index < tasks.size(); index++) {
            Pair<UUID, AsyncRunner.Task> i = tasks.get(index);
            message = message.append(Text.literal(String.format("• %s - ", i.getRight()
                            .getName())))
                    .append(Text.literal(i.getLeft()
                                    .toString()
                                    .substring(0, 5))
                            .formatted(Formatting.UNDERLINE)
                            .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                                            i.getLeft()
                                                    .toString()))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            Text.of(i.getLeft() + "\nClick to copy UUID")))))
                    .append(Text.literal(" [-]")
                            .formatted(Formatting.GOLD)
                            .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                            String.format("/util task stop %s", i.getLeft())))
                                    .withHoverEvent(new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT, Text.of("Tries to stop the task")))))
                    .append(Text.literal(" [X]")
                            .formatted(Formatting.RED)
                            .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                            String.format("/util task kill %s", i.getLeft())))
                                    .withHoverEvent(new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT, Text.of("Force kills the task")))))
                    .append(index + 1 >= tasks.size() ? Text.empty() : Text.literal("\n"));
        }

        context.getSource()
                .getPlayer()
                .sendMessage(message);
        return 0;
    }

    private static int onStop(CommandContext<FabricClientCommandSource> context) {
        String uuid = getString(context, "uuid");
        AsyncRunner.stop(UUID.fromString(uuid));
        return 0;
    }

    private static int onKill(CommandContext<FabricClientCommandSource> context) {
        String uuid = getString(context, "uuid");
        AsyncRunner.kill(UUID.fromString(uuid));
        return 0;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("task")
                        .then(ClientCommandManager.literal("list")
                                .executes(Task::onList))
                        .then(ClientCommandManager.literal("stop")
                                .then(ClientCommandManager.argument("uuid", string())
                                        .suggests((c, b) -> suggestMatching(AsyncRunner.getUuidList(), b))
                                        .executes(Task::onStop)))
                        .then(ClientCommandManager.literal("kill")
                                .then(ClientCommandManager.argument("uuid", string())
                                        .suggests((c, b) -> suggestMatching(AsyncRunner.getUuidList(), b))
                                        .executes(Task::onKill)))));
    }
}
