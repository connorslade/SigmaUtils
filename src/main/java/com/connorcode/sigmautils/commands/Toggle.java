package com.connorcode.sigmautils.commands;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.module.Module;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

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
                                .suggests(((context, builder) -> suggestMatching(SigmaUtils.modules.values().stream()
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
        Optional<Module> module = SigmaUtils.modules.values().stream()
                .filter(m -> Objects.equals(m.id, moduleId))
                .findFirst();

        if (module.isEmpty()) {
            player.sendMessage(Text.of("[-] Invalid Module ID"), false);
            return 1;
        }

        if (setState) {
            module.get()
                    .enable(MinecraftClient.getInstance());
            return 0;
        }

        module.get()
                .disable(MinecraftClient.getInstance());
        return 0;
    }
}
