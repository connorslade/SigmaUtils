package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.BetterSplashes;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
    @Shadow
    @Final
    private static Random RANDOM;
    @Shadow
    @Final
    private List<String> splashTexts;

    @Inject(method = "get", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", shift = At.Shift.BEFORE), cancellable = true)
    void get(CallbackInfoReturnable<SplashTextRenderer> cir) {
        if (!Config.getEnabled(BetterSplashes.class))
            return;

        ArrayList<String> totalSplashes = new ArrayList<>();
        totalSplashes.addAll(splashTexts);
        totalSplashes.addAll(BetterSplashes.betterSplashes);
        cir.setReturnValue(new SplashTextRenderer(totalSplashes.get(RANDOM.nextInt(totalSplashes.size()))));
    }
}
