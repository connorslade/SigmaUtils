package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.PacketSendCallback;
import com.connorcode.sigmautils.event.ScreenOpenCallback;
import com.connorcode.sigmautils.event.Tick;
import com.connorcode.sigmautils.mixin.AbstractSignEditScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class AutoSign extends Module {
    SignTextSetting signText = new SignTextSetting();
    LinkedList<Packet<?>> packets = new LinkedList<>();
    int packetCooldown;

    public AutoSign() {
        super("auto_sign", "Auto Sign", "Automatically writes text on signs you place. (/set auto_sign text \"Hi\")",
                Category.Misc);
    }

    public void init() {
        super.init();

        ScreenOpenCallback.EVENT.register(screen -> {
            if (!(screen.get() instanceof AbstractSignEditScreen && enabled)) return;
            SignBlockEntity signBlock = ((AbstractSignEditScreenAccessor) screen.get()).getSign();
            this.signText.updateSign(signBlock);
            screen.cancel();
        });

        Tick.GameTickCallback.EVENT.register(tickEvent -> {
            if (!enabled || packets.isEmpty() || --packetCooldown > 0) return;

            var handler = Objects.requireNonNull(client.player).networkHandler;
            client.player.sendMessage(Text.of("SENDING PACKET"));
            handler.sendPacket(packets.pop());
            client.player.sendMessage(Text.of(String.format("PACKETS LEFT: %d", packets.size())));
            packetCooldown = 20 * 3;
        });

        PacketSendCallback.EVENT.register(packet -> {
            if (client.player == null || packet.get() instanceof EntityTrackerUpdateS2CPacket ||
                    packet.get() instanceof EntityPositionS2CPacket ||
                    packet.get() instanceof WorldTimeUpdateS2CPacket ||
                    packet.get() instanceof EntityVelocityUpdateS2CPacket ||
                    packet.get() instanceof PlaySoundS2CPacket ||
                    packet.get() instanceof MapUpdateS2CPacket ||
                    packet.get() instanceof KeepAliveS2CPacket ||
                    packet.get() instanceof PlayerMoveC2SPacket ||
                    packet.get() instanceof EntitySetHeadYawS2CPacket || packet.get() instanceof EntityS2CPacket)
                return;
            client.player.sendMessage(Text.of(String.format("SENDING %s", packet.get().getClass().getSimpleName())));

            if (!(packet.get() instanceof UpdateSignC2SPacket sign)) return;

            var pos = sign.getPos();
            var text = sign.getText();
            var info =
                    String.format("(%d, %d, %d) - [%s, %s, %s, %s] - %s", pos.getX(), pos.getY(), pos.getZ(), text[0],
                            text[1], text[2], text[3], sign.isFront() ? "Front" : "Back");
            Objects.requireNonNull(client.player).sendMessage(Text.of(info));
        });
    }

    class SignTextSetting {
        StringSetting[] frontLines = new StringSetting[4];
        StringSetting[] backLines = new StringSetting[4];

        public SignTextSetting() {
            frontLines[0] = lineSetting(1, true).value("Hello, world!").build();
            frontLines[1] = lineSetting(2, true).value("- SigmaUtils").build();

            for (int i = 3; i <= 4; i++) frontLines[i - 1] = lineSetting(i, true).build();
            for (int i = 1; i <= 4; i++) backLines[i - 1] = lineSetting(i, false).build();
        }

        private static StringSetting lineSetting(int line, boolean side) {
            return new StringSetting(AutoSign.class, "")
                    .category("Sign Text - " + (side ? "Front" : "Back"))
                    .id("line_" + line + "_" + (side ? "front" : "back"))
                    .showName(false);
        }

        boolean hasFrontLines() {
            for (StringSetting line : frontLines) if (!line.value().isEmpty()) return true;
            return false;
        }

        boolean hasBackLines() {
            for (StringSetting line : backLines) if (!line.value().isEmpty()) return true;
            return false;
        }

        void updateSign(SignBlockEntity sign) {
            if (this.hasFrontLines())
                AutoSign.this.packets.add(
                        new UpdateSignC2SPacket(sign.getPos(), true, frontLines[0].value(), frontLines[1].value(),
                                frontLines[2].value(), frontLines[3].value()));
            if (this.hasBackLines())
                AutoSign.this.packets.add(
                        new UpdateSignC2SPacket(sign.getPos(), false, backLines[0].value(), backLines[1].value(),
                                backLines[2].value(), backLines[3].value()));
        }
    }
}
