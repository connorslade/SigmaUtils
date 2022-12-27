package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.event.ScreenOpenCallback;
import com.connorcode.sigmautils.mixin.SignEditScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;

import java.util.Arrays;
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class AutoSign extends Module {
    public static final String[] lines = new String[]{
            "",
            "",
            "",
            ""
    };

    public AutoSign() {
        super("auto_sign", "Auto Sign", "Automatically writes text on signs you place. (/set auto_sign text \"Hi\")",
                Category.Misc);
    }

    int commandExecute(CommandContext<FabricClientCommandSource> ctx, int len) {
        for (int i = 0; i < 4; i++) {
            if (i >= len) lines[i] = "";
            else lines[i] = getString(ctx, String.format("line%d", i + 1));
        }
        return 0;
    }

    public void init() {
        // wow look at that code formatting
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("util")
                        .then(ClientCommandManager.literal("set")
                                .then(ClientCommandManager.literal(id)
                                        .then(ClientCommandManager.literal("lines")
                                                .then(ClientCommandManager.argument("line1", string())
                                                        .executes(c -> commandExecute(c, 1))
                                                        .then(ClientCommandManager.argument("line2", string())
                                                                .executes(c -> commandExecute(c, 2))
                                                                .then(ClientCommandManager.argument("line3", string())
                                                                        .executes(c -> commandExecute(c, 3))
                                                                        .then(ClientCommandManager.argument("line4",
                                                                                        string())
                                                                                .executes(c -> commandExecute(c,
                                                                                        4)))))))))));
        ScreenOpenCallback.EVENT.register(screen -> {
            if (!(screen instanceof SignEditScreen && enabled)) return false;
            SignBlockEntity signBlock = ((SignEditScreenAccessor) screen).getSign();
            Objects.requireNonNull(MinecraftClient.getInstance().player).networkHandler.sendPacket(
                    new UpdateSignC2SPacket(signBlock.getPos(), lines[0], lines[1], lines[2], lines[3]));
            return true;
        });
    }

    public void loadConfig(NbtCompound config) {
        super.loadConfig(config);
        NbtList linesConfig = config.getList("lines", NbtElement.STRING_TYPE);
        lines[0] = linesConfig.getString(0);
        lines[1] = linesConfig.getString(1);
        lines[2] = linesConfig.getString(2);
        lines[3] = linesConfig.getString(3);
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = super.saveConfig();
        NbtList linesConfig = new NbtList();
        linesConfig.addAll(Arrays.stream(lines)
                .map(NbtString::of)
                .toList());
        nbt.put("lines", linesConfig);
        return nbt;
    }
}
