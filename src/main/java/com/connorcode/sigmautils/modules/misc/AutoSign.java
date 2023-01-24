package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.ScreenOpenCallback;
import com.connorcode.sigmautils.mixin.SignEditScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;

import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class AutoSign extends Module {
    public static StringSetting line1 = lineSetting(1).value("Hello, world!")
            .build();
    public static StringSetting line2 = lineSetting(2).value("- SigmaUtils")
            .build();
    public static StringSetting line3 = lineSetting(3).build();
    public static StringSetting line4 = lineSetting(4).build();

    public AutoSign() {
        super("auto_sign", "Auto Sign", "Automatically writes text on signs you place. (/set auto_sign text \"Hi\")",
                Category.Misc);
    }

    private static StringSetting lineSetting(int line) {
        return new StringSetting(AutoSign.class, "line" + line).category("Sign Text")
                .showName(false);
    }

    public void init() {
        super.init();
        ScreenOpenCallback.EVENT.register(screen -> {
            if (!(screen.get() instanceof SignEditScreen && enabled)) return;
            SignBlockEntity signBlock = ((SignEditScreenAccessor) screen.get()).getSign();
            Objects.requireNonNull(client.player).networkHandler.sendPacket(
                    new UpdateSignC2SPacket(signBlock.getPos(), line1.value(), line2.value(), line3.value(),
                            line4.value()));
            screen.cancel();
        });
    }
}
