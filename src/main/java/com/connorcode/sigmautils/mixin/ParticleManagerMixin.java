package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.rendering.NoBreakParticles;
import com.connorcode.sigmautils.modules.rendering.ParticleControl;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
    private void onAddBlockDestroyEffects(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (Config.getEnabled(NoBreakParticles.class)) ci.cancel();
    }

    @Redirect(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addParticle(Lnet/minecraft/client/particle/Particle;)V"))
    void onAddParticle(ParticleManager particleManager, Particle particle, ParticleEffect parameters) {
        if (Config.getEnabled(ParticleControl.class) && ParticleControl.disabled(parameters.getType())) return;
        particleManager.addParticle(particle);
    }
}
