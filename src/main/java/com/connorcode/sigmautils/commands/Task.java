package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.misc.AsyncRunner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
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
        StringBuilder message = new StringBuilder();

        if (tasks.isEmpty()) message.append("No tasks are running");
        for (Pair<UUID, AsyncRunner.Task> i : tasks) {
            message.append("• ")
                    .append(i.getRight()
                            .getName())
                    .append(" - ")
                    .append(i.getLeft()
                            .toString(), 0, 5)
                    .append("\n");
        }

        context.getSource()
                .getPlayer()
                .sendMessage(Text.of(message.toString()
                        .trim()));
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
