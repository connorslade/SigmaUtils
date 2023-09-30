package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.SeeThruLoading;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    // TODO: See if this works
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (Config.getEnabled(SeeThruLoading.class))
            context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        else this.renderBackground(context, mouseX, mouseY, delta);

        for (var i : this.drawables) {
            i.render(context, mouseX, mouseY, delta);
        }
    }
}
