package com.connorcode.sigmautils.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    // TODO: Fix this
//    @Inject(method = "initWidgets", at = @At(value = "INVOKE", target = "", ordinal = 8, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
//    void onDisconnectButton() {
//        if (!Config.getEnabled(ConfirmDisconnect.class))
//            return;
//        ci.cancel();
//
//        this.addDrawableChild(
//                new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 - 16, 204, 20, text, (button) -> {
//                    button.active = false;
//                    assert this.client != null;
//                    this.client.setScreen(new ConfirmDisconnect.ConfirmDisconnectScreen());
//                }));
//    }
}
