package com.connorcode.sigmautils.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Fotd implements Command {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("fotd")
                        .executes(this::execute)));
    }

    int execute(CommandContext<FabricClientCommandSource> context) {
        try {
            URL url = new URL("https://fotd.connorcode.com/api/fact");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "text/plain");
            InputStream responseStream = connection.getInputStream();
            Scanner scanner = new Scanner(responseStream, StandardCharsets.UTF_8.name());
            context.getSource()
                    .getPlayer()
                    .sendMessage(Text.of(String.format("FOTD: %s", scanner.nextLine())));
        } catch (IOException ignore) {
        }
        return 0;
    }
}
