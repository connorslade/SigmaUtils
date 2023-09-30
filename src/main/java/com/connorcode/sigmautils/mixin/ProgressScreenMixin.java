package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.SeeThruLoading;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProgressScreen.class)
public class ProgressScreenMixin extends Screen {
    protected ProgressScreenMixin(Text title) {
        super(title);
    }

    // For see_through_loading
    @Inject(method = "shouldCloseOnEsc", at = @At("HEAD"), cancellable = true)
    void ohShouldCloseOnEsc(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(Config.getEnabled(SeeThruLoading.class) && SeeThruLoading.escToClose.value());
    }

    // For see_through_loading
    // TODO: FIX SEE THROUGH LOADING
//    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ProgressScreen;renderBackground(Lnet/minecraft/client/gui/DrawContext;)V"))
//    void onRenderBackground(ProgressScreen instance, DrawContext matrixStack) {
//        if (!Config.getEnabled(SeeThruLoading.class))
//            this.renderBackground(matrixStack);
//    }
//
//    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ProgressScreen;renderBackground(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.BEFORE))
//    void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//        if (Config.getEnabled(SeeThruLoading.class))
//            context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
//    }
}
