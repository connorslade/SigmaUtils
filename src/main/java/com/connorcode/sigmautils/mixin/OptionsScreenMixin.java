package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.UiTweaks;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin {
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/OptionsScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 5))
    Element onSoundButtonRender(OptionsScreen instance, Element element) {
        var sa = ((ScreenAccessor) instance);
        sa.invokeAddDrawableChild((Element & Drawable & Selectable) element);
        if (!Config.getEnabled(UiTweaks.class) || !UiTweaks.audioMuteButton.value()) return element;

        var padding = getPadding();
        var button = ((ButtonWidget) element);
        button.setWidth(150 - 20 - padding);
        sa.invokeAddDrawableChild(new ButtonWidget(button.x + 150 - 20, button.y, 20, 20, Text.of(UiTweaks.muted ? "U" : "M"), (buttonWidget) -> {
            UiTweaks.muted ^= true;
            buttonWidget.setMessage(Text.of(UiTweaks.muted ? "U" : "M"));
        }));
        return element;
    }
}
