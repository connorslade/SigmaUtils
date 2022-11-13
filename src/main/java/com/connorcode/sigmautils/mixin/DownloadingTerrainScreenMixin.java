package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.SeeThruLoading;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DownloadingTerrainScreen.class)
public class DownloadingTerrainScreenMixin extends Screen {
    protected DownloadingTerrainScreenMixin(Text title) {
        super(title);
    }

    // For see_through_loading
    @Inject(method = "shouldCloseOnEsc", at = @At("HEAD"), cancellable = true)
    void ohShouldCloseOnEsc(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(Config.getEnabled(SeeThruLoading.class));
    }

    // For see_through_loading
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen;renderBackgroundTexture(I)V"))
    void onRenderBackground(DownloadingTerrainScreen instance, int i) {
        if (!Config.getEnabled(SeeThruLoading.class)) this.renderBackgroundTexture(0);
    }

    @Inject(method = "render", at = @At("HEAD"))
    void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Config.getEnabled(SeeThruLoading.class))
            this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
    }
}
