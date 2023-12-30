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

    static int serverPackInstall(CommandContext<FabricClientCommandSource> context) {
        String urlRaw = getString(context, "url");
        UUID uuid = null;

        try {
            uuid = UUID.fromString(getString(context, "uuid"));
        } catch (IllegalArgumentException ignored) {}

        URL url;
        try {
            url = new URL(urlRaw);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            context.getSource().sendError(Text.of(String.format("Invalid url! (%s)", e.getMessage())));
            return 0;
        }

        var packs = client.getServerResourcePackProvider();
        packs.addResourcePack(uuid == null ? UUID.randomUUID() : uuid, url, null);
        return 0;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("resourcepack")
                        .then(ClientCommandManager.literal("server")
                                .then(ClientCommandManager.literal("install")
                                          .then(ClientCommandManager.argument("url", string())
                                                    .executes(ResourcePack::serverPackInstall)
                                                    .then(ClientCommandManager.argument("uuid", string()).executes(ResourcePack::serverPackInstall))))
                                .then(ClientCommandManager.literal("remove")
                                        .executes(ResourcePack::serverPackUninstall)))));
    }
}
