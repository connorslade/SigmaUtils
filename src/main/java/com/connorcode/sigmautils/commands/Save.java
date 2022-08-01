package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.config.Config;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.io.IOException;

public class Save implements Command {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("save")
                        .executes(ctx -> {
                            try {
                                Config.save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return 0;
                        })));
    }
}
