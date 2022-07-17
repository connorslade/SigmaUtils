package com.connorcode.sigmautils.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface Command {
    void register(CommandDispatcher<FabricClientCommandSource> dispatcher);
}
