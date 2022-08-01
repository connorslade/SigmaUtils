package com.connorcode.sigmautils.misc;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface ModuleSettingSetter {
    int set(CommandContext<FabricClientCommandSource> context);
}
