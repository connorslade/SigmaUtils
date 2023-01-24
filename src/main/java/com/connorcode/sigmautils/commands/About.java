package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.SigmaUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class About implements Command {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("about")
                        .executes(context -> {
                            Objects.requireNonNull(client.player)
                                    .sendMessage(
                                            Text.of(String.format("Σ] Sigma Utils v%s", SigmaUtils.VERSION)),
                                            false);
                            return 0;
                        })));
    }
}
