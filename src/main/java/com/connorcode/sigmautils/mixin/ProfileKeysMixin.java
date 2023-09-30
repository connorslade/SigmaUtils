package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.chat.NoChatSignatures;
import net.minecraft.client.session.ProfileKeysImpl;
import net.minecraft.network.encryption.Signer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProfileKeysImpl.class)
public class ProfileKeysMixin {
    @Inject(method = "fetchKeyPair()Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    private void onGetSigner(CallbackInfoReturnable<Signer> cir) {
        if (Config.getEnabled(NoChatSignatures.class))
            cir.setReturnValue(null);
    }
}
