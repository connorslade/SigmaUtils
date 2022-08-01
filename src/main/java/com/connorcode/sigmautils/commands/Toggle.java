package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.module.Module;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.CommandSource.suggestMatching;

public class Toggle implements Command {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("util")
                .then(ClientCommandManager.literal("toggle")
                        .then(ClientCommandManager.argument("module", string())
                                .suggests(((context, builder) -> suggestMatching(SigmaUtilsClient.modules.stream()
                                        .map(m -> m.id)
                                        .toList(), builder)))
                                .then(ClientCommandManager.argument("state", bool())
                                        .executes(this::execute)))));
    }

    int execute(CommandContext<FabricClientCommandSource> context) {
        String moduleId = getString(context, "module");
        boolean setState = getBool(context, "state");
        ClientPlayerEntity player = Objects.requireNonNull(context.getSource()
                .getClient().player);
        Optional<Module> module = SigmaUtilsClient.modules.stream()
                .filter(m -> Objects.equals(m.id, moduleId))
                .findFirst();

        if (module.isEmpty()) {
            player.sendMessage(Text.of("[-] Invalid Module ID"), false);
            return 1;
        }

        module.get().enabled = setState;
        player.sendMessage(
                Text.of(String.format("[*] %s module `%s`", setState ? "Enabled" : "Disabled", module.get().name)),
                false);

        try {
            Config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
