package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.misc.Datatypes;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;


public class ForceFly extends Module {
    static int flyTickCutoff = 60;

    static int flyTicks = 0;

    public ForceFly() {
        super("force_fly", "Force Allow Flying", "Forces the client to allow you to fly ingame.", Category.Misc);
    }

    @Override
    public void init() {
        ClientCommandRegistrationCallback.EVENT.register(
                ((dispatcher, registryAccess) -> Util.moduleConfigCommand(dispatcher, this, "flyTickCutoff",
                        Datatypes.Integer, context -> {
                            flyTickCutoff = getInteger(context, "setting");
                            return 0;
                        })));
    }

    @Override
    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!enabled || client.player == null || client.player.isOnGround()) {
            flyTicks = 0;
            return;
        }

        flyTicks++;
        if (flyTicks < flyTickCutoff) return;
        flyTicks = 0;
        client.player.sendMessage(Text.of("GOING DOWN #" + System.currentTimeMillis()));
        Objects.requireNonNull(client.getNetworkHandler())
                .sendPacket(
                        new PlayerMoveC2SPacket.PositionAndOnGround(client.player.getX(), client.player.getY() - 0.0433,
                                client.player.getX(), false));
    }

    @Override
    public NbtCompound saveConfig() {
        NbtCompound nbt = super.saveConfig();
        nbt.putInt("flyTickCutoff", flyTickCutoff);
        return nbt;
    }

    @Override
    public void loadConfig(NbtCompound config) {
        super.loadConfig(config);
        flyTickCutoff = config.contains("flyTickCutoff") ? config.getInt("flyTickCutoff") : 60;
    }
}
