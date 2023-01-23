package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.misc.util.NetworkUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;

import static com.connorcode.sigmautils.SigmaUtils.directory;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.CommandSource.suggestMatching;

public class Dump implements Command {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("dump")
                        .then(ClientCommandManager.argument("resource", string())
                                .suggests(((context, builder) -> suggestMatching(new String[]{"packets"}, builder)))
                                .executes(ctx -> {
                                    try {
                                        return execute(ctx);
                                    } catch (IOException e) {
                                        ctx.getSource().sendError(Text.of("Error: " + e.getMessage()));
                                    }
                                    return 0;
                                }))));
    }

    int execute(CommandContext<FabricClientCommandSource> context) throws IOException {
        var resource = getString(context, "resource");

        var dumps = directory.resolve("dump").toFile();
        dumps.mkdirs();

        if (resource.equals("packets")) {
            StringBuilder out = new StringBuilder();
            var mr = FabricLoader.getInstance().getMappingResolver();
            for (var i : NetworkUtils.getPackets().keySet())
                out.append(mr.unmapClassName("intermediary", i.getName()))
                        .append(" ")
                        .append(i.getSimpleName())
                        .append("\n");
            Files.write(dumps.toPath().resolve("packets.txt"), out.toString().getBytes());
        }

        return 0;
    }
}
