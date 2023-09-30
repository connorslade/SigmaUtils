package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.misc.Player;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;


public class PlayerCommand implements Command {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("player")
                        .then(ClientCommandManager.literal("attack")
                                .then(ClientCommandManager.literal("continuous").executes((arg) -> {
                                    Player.player.addAction(new Player.AttackAction(Player.InteractTime.continuous()));
                                    return 0;
                                })).then(ClientCommandManager.literal("once").executes((arg) -> {
                                    Player.player.addAction(new Player.AttackAction(Player.InteractTime.once()));
                                    return 0;
                                })).then(ClientCommandManager.literal("interval").then(ClientCommandManager.argument("ticks", integer()).executes((arg) -> {
                                    var ticks = getInteger(arg, "ticks");
                                    Player.player.addAction(new Player.AttackAction(Player.InteractTime.interval(ticks)));
                                    return 0;
                                }))))
                        .then(ClientCommandManager.literal("stop").executes((arg) -> {
                            Player.player.clearActions();
                            return 0;
                        }))));
    }
}
