package com.connorcode.sigmautils.mixin;

import net.minecraft.client.gui.screen.option.OptionsScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin {
    // TODO: Fix this
//    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/OptionsScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 5))
//    Element onSoundButtonRender(OptionsScreen instance, Element element) {
//        Util.addChild(instance, (Drawable) element);
//        if (!Config.getEnabled(UiTweaks.class) || !UiTweaks.audioMuteButton.value())
//            return element;
//
//        var padding = getPadding();
//        var button = ((ButtonWidget) element);
//        button.setWidth(150 - 20 - padding);
//        Util.addChild(instance,
//                new ButtonWidget(button.x + 150 - 20, button.y, 20, 20, Text.of(UiTweaks.muted ? "U" : "M"),
//                        (buttonWidget) -> {
//                            UiTweaks.muted ^= true;
//                            buttonWidget.setMessage(Text.of(UiTweaks.muted ? "U" : "M"));
//                        }));
//        return element;
//    }
//
//    @Inject(method = "init", at = @At("TAIL"))
//    void onOptionScreenInit(CallbackInfo ci) {
//        if (!Config.getEnabled(UiTweaks.class) || !UiTweaks.audioMuteButton.value()) return;
//
//    }
}
