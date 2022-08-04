package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.ConfigGui;
import com.connorcode.sigmautils.misc.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    void init(CallbackInfo ci) {
        ScreenAccessor screen = ((ScreenAccessor) this);
        Util.addDrawable((Screen) (Object) this,
                new ButtonWidget(screen.getWidth() / 2 - 100 - 24, screen.getHeight() / 4 + 48 + 24, 20, 20,
                        Text.of("Σ"), button -> MinecraftClient.getInstance()
                        .setScreen(new ConfigGui()),
                        ((button, matrices, mouseX, mouseY) -> screen.invokeRenderOrderedTooltip(matrices,
                                Language.getInstance()
                                        .reorder(List.of(Text.of("Sigma Utils"))), mouseX, mouseY))));
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/TitleScreen;isMinceraft:Z", opcode = Opcodes.GETFIELD))
    boolean isMinecraft(TitleScreen instance) {
        return Config.getEnabled("minceraft");
    }
}
