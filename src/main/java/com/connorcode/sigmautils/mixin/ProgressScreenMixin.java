package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.SeeThruLoading;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProgressScreen.class)
public abstract class ProgressScreenMixin extends Screen {
    @Shadow
    public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);

    protected ProgressScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "shouldCloseOnEsc", at = @At("HEAD"), cancellable = true)
    void ohShouldCloseOnEsc(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(Config.getEnabled(SeeThruLoading.class) && SeeThruLoading.escToClose.value());
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    void onRenderBackground(Screen instance, DrawContext context, int mouseX, int mouseY, float delta) {
        if (!Config.getEnabled(SeeThruLoading.class)) this.renderBackground(context, mouseX, mouseY, delta);
        else context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);

        for (var i : this.drawables) i.render(context, mouseX, mouseY, delta);
    }
}
