package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.network.PacketReceiveCallback;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;

import java.util.regex.Pattern;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class BridgeAnalysis extends Module {
    final StringSetting endRegex =
            new StringSetting(BridgeAnalysis.class, "End Regex").description(
                            "Regex to match end messages. Triggers wdl.")
                    .value(".*(VICTORY|GAME OVER)!")
                    .build();
    final StringSetting saveLocation =
            new StringSetting(BridgeAnalysis.class, "Save Location").description(
                            "The folder to save the world downloads into.")
                    .value("bridge_analysis")
                    .build();

    public BridgeAnalysis() {
        super("bridge_analysis", "Bridge Analysis",
                "Downloads the wool bridges from the world after a game of hypixel bedwars.", Category.Server);
    }

    @Override
    public void init() {
        super.init();

        PacketReceiveCallback.EVENT.register(packet -> {
            if (!this.enabled) return;
            if (!(packet.get() instanceof TitleS2CPacket title)) return;
            var regex = Pattern.compile(endRegex.value());
            if (!regex.matcher(title.getTitle().getString()).matches()) {}

            // wdl
        });
    }

    void download() {
        assert client.world != null;
        var chunks = client.world.getChunkManager().chunks.chunks;

        for (var i = 0; i < chunks.length(); i++) {
            var chunk = chunks.get(i);
//            chunk.
        }
    }
}
