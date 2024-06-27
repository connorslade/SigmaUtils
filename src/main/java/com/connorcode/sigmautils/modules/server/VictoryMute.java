package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.DummySetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;

import java.util.regex.Pattern;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.modules.meta.Padding.getPadding;

@ModuleInfo(description = "Automatically mute the game when you finish a game of bedwars on hypixel",
        documentation = "Kinda a weird module, I know. I made it because when a game of Bedwars on Hypixel it plays a lot of sounds and it gets annoying after a while. You can probably also use this module for other game modes by expanding the Regex.")
public class VictoryMute extends Module {
    public static final NumberSetting reduction =
            new NumberSetting(VictoryMute.class, "Reduction", 0, 1).description(
                            "Reduction in volume when muted (Percentage)")
                    .enforceBounds(true)
                    .value(1)
                    .build();
    public static boolean muted = false;
    final StringSetting victoryRegex =
            new StringSetting(VictoryMute.class, "Victory Regex").description("Regex to match victory messages")
                    .value(".*(VICTORY|GAME OVER)!")
                    .build();

    @Override
    public void init() {
        new DummySetting(VictoryMute.class, "Active", 0) {
            @Override
            public int initRender(Screen screen, int x, int y, int width) {
                if (!muted || !enabled) return 0;
                return client.textRenderer.fontHeight + getPadding() * 2;
            }

            @Override
            public void render(RenderData data, int x, int y) {
                if (!muted || !enabled) return;
                data.drawContext()
                        .drawText(client.textRenderer, TextStyle.Color.LightPurple.code() + "Currently Muted", x,
                                y + getPadding(), 0, false);
            }
        }.build();
        super.init();
    }

    @EventHandler
    void onPacketReceive(PacketReceiveEvent packet) {
        if (!this.enabled) return;
        if (packet.get() instanceof TitleS2CPacket title) {
            // recompileing regex every time is fiiiiine
            // just ignore the following todo
            // TODO: Let settings convert to different types (callback on builder)
            var regex = Pattern.compile(victoryRegex.value());
            muted = regex.matcher(title.text().getString()).matches();
        }

        if (packet.get() instanceof GameJoinS2CPacket) muted = false;
    }

    @Override
    public void disable() {
        super.disable();
        muted = false;
    }
}
