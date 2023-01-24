package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;


public class ForceFly extends Module {
    static NumberSetting flyTickCutoff = new NumberSetting(ForceFly.class, "flyTickCutoff", 0, 100).value(60)
            .description(
                    "The amount of ticks to wait before dropping the player (to prevent getting kicked for flying)")
            .precision(0)
            .build();

    static int flyTicks = 0;

    public ForceFly() {
        super("force_fly", "Force Allow Flying", "Forces the client to allow you to fly ingame.", Category.Misc);
    }

    @Override
    public void tick() {
        if (!enabled || client.player == null || client.player.isOnGround()) {
            flyTicks = 0;
            return;
        }

        flyTicks++;
        if (flyTicks < flyTickCutoff.intValue()) return;
        flyTicks = 0;
        client.player.sendMessage(Text.of("GOING DOWN #" + System.currentTimeMillis()));
        Objects.requireNonNull(client.getNetworkHandler())
                .sendPacket(
                        new PlayerMoveC2SPacket.PositionAndOnGround(client.player.getX(), client.player.getY() - 0.0433,
                                client.player.getX(), false));
    }
}
