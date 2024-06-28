package com.connorcode.sigmautils.mixin;


import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Text title) {
        super(title);
    }

//    @Inject(method = "init", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
//    void onButtonRender(CallbackInfo ci, GridWidget gridWidget, GridWidget.Adder adder) {
//        if (!Config.getEnabled(UiTweaks.class) || !UiTweaks.audioMuteButton.value()) return;
//
//        var x = this.width / 2 + 160;
//        var y = this.height / 6 + 42;
//        addDrawableChild(ButtonWidget.builder(Text.of(UiTweaks.muted ? "U" : "M"), button -> {
//            UiTweaks.muted ^= true;
//            button.setMessage(Text.of(UiTweaks.muted ? "U" : "M"));
//        }).position(x, y).size(20, 20).build());
//    }
}
