package com.connorcode.sigmautils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class ResourcePack implements Command {
    static int serverPackUninstall(CommandContext<FabricClientCommandSource> context) {
        Objects.requireNonNull(client.getServerResourcePackProvider())
                .clear();
        return 0;
    }

    int serverPackInstall(CommandContext<FabricClientCommandSource> context, boolean isHash) {
        String urlRaw = getString(context, "url");
        String hash = isHash ? getString(context, "hash") : null;
        URL url;
        try {
            url = new URL(urlRaw);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            context.getSource()
                    .sendError(Text.of(String.format("Invalid url! (%s)", e.getMessage())));
            return 0;
        }

        client.getServerResourcePackProvider().addResourcePack(UUID.randomUUID(), url, hash);
        return 0;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("resourcepack")
                        .then(ClientCommandManager.literal("server")
                                .then(ClientCommandManager.literal("install")
                                        .then(ClientCommandManager.argument("url", string())
                                                .executes(ctx -> serverPackInstall(ctx, false))
                                                .then(ClientCommandManager.argument("hash", string())
                                                        .executes(ctx -> serverPackInstall(ctx, true)))))
                                .then(ClientCommandManager.literal("remove")
                                        .executes(ResourcePack::serverPackUninstall)))));
    }
}
