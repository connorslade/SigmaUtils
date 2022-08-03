package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Objects;

public class About implements Command {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("about")
                        .executes(context -> {
                            Objects.requireNonNull(MinecraftClient.getInstance().player)
                                    .sendMessage(
                                            Text.of(String.format("Σ] Sigma Utils v%s", SigmaUtilsClient.version)),
                                            false);
                            return 0;
                        })));
    }
}
