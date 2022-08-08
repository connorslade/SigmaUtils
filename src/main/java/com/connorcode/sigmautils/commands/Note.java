package com.connorcode.sigmautils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class Note implements Command {
    private static int execute(CommandContext<FabricClientCommandSource> context, boolean isLocation) {
        String message = getString(context, "text");
        boolean actionBar = isLocation && getBool(context, "actionBar");
        context.getSource()
                .getPlayer()
                .sendMessage(Text.of(message), actionBar);
        return 0;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("note")
                        .then(ClientCommandManager.argument("text", string())
                                .executes(ctx -> execute(ctx, false))
                                .then(ClientCommandManager.argument("actionBar", bool())
                                        .executes(ctx -> execute(ctx, true))))
                ));
    }
}
