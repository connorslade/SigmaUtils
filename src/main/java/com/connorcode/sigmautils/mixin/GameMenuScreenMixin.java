package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.ConfirmDisconnect;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V", ordinal = 8, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    void onDisconnectButton(CallbackInfo ci, int i, int j, String string, ButtonWidget buttonWidget, Text text) {
        if (!Config.getEnabled(ConfirmDisconnect.class))
            return;
        ci.cancel();

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 - 16, 204, 20, text, (button) -> {
            button.active = false;
            assert this.client != null;
            this.client.setScreen(new ConfirmDisconnect.ConfirmDisconnectScreen());
        }));
    }
}
