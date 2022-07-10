package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.ConfigGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    void init(CallbackInfo ci) {
        ScreenAccessor screen = ((ScreenAccessor) this);
        screen.invokeAddDrawableChild(
                new ButtonWidget(screen.getWidth() / 2 - 100 - 24, screen.getHeight() / 4 + 48 + 24, 20, 20,
                        Text.of("Σ"),
                        button -> MinecraftClient.getInstance()
                                .setScreen(new ConfigGui())));
    }
}
