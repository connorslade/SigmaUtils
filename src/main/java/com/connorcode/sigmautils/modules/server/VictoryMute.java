package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class VictoryMute extends Module {
    public static boolean muted = false;
    final StringSetting victoryRegex =
            new StringSetting(VictoryMute.class, "Victory Regex").description("Regex to match victory messages")
                    .value(".*VICTORY!")
                    .build();

    public VictoryMute() {
        super("victory_mute", "Victory Mute", "Automatically mute the game when you win a game of bedwars on hypixel",
                Category.Server);
    }

    @Override
    public void init() {
        super.init();

        PacketReceiveCallback.EVENT.register(packet -> {
            if (!this.enabled) return;
            if (packet.get() instanceof TitleS2CPacket title) {
                System.out.println("Got title packet " + title.getTitle().getString());
                var regex = Pattern.compile(victoryRegex.value());
                muted = regex.matcher(title.getTitle().getString()).matches();
                System.out.println("Muted: " + muted);
            }

            if (packet.get() instanceof GameJoinS2CPacket) muted = false;
        });
    }

    @Override
    public void disable(MinecraftClient client) {
        super.disable(client);
        muted = false;
    }
}
