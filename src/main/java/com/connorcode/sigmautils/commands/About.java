package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.SigmaUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;


public class About implements Command {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                                    .then(ClientCommandManager.literal("about")
                                                  .executes(context -> {
                                                      var gameVersion = client.getGameVersion();
                                                      var fabricVersion = FabricLoader.getInstance()
                                                              .getModContainer("fabricloader")
                                    .orElseThrow()
                                    .getMetadata()
                                    .getVersion()
                                    .getFriendlyString();
                            var fabricDebug = FabricLoader.getInstance().isDevelopmentEnvironment() ? " (debug)" : "";
                            var sigmaUtilsVersion = SigmaUtils.VERSION;
                            var protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();

                            var message =
                                    Text.of(String.format("SigmaUtils v%s\nFabric v%s%s\nMinecraft v%s\nProtocol v%s",
                                            sigmaUtilsVersion, fabricVersion, fabricDebug, gameVersion,
                                            protocolVersion));

                            Objects.requireNonNull(client.player).sendMessage(message, false);
                            return 0;
                        })));
    }
}
