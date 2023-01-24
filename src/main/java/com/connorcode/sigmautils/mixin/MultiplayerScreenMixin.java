package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.util.NetworkUtils;
import com.connorcode.sigmautils.modules._interface.UiTweaks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    void onInit(CallbackInfo ci) {
        if (!UiTweaks.showValidSession()) return;
        addDrawableChild(new Components.DrawableElement() {
            @Override
            public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                var text = NetworkUtils.getAuthStatus().getText();
                Objects.requireNonNull(client).textRenderer.draw(matrices, text,
                        width - client.textRenderer.getWidth(text) - 10, 10, 0xFFFFFF);
            }
        });
    }
}
