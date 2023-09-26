package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.misc.Player;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class PlayerCommand implements Command {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("player")
                        .then(ClientCommandManager.literal("dbg")
                                .executes((arg) -> {
                                    arg.getSource().sendFeedback(Text.literal("Attacking Continuously"));
                                    Player.player.addAction(new Player.AttackAction(Player.InteractTime.continuous()));
                                    return 0;
                                }))
                        .then(ClientCommandManager.literal("stop").executes((arg) -> {
                            Player.player.clearActions();
                            return 0;
                        }))));
    }
}
