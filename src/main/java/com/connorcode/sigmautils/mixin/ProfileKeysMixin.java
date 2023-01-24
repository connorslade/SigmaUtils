package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.chat.NoChatSignatures;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.Signer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ProfileKeys.class)
public class ProfileKeysMixin {
    @Inject(method = "getSigner", at = @At("HEAD"), cancellable = true)
    private void onGetSigner(CallbackInfoReturnable<Signer> cir) {
        if (Config.getEnabled(NoChatSignatures.class))
            cir.setReturnValue(null);
    }

    @Inject(method = "getPublicKey", at = @At("HEAD"), cancellable = true)
    private void onGetPublicKey(CallbackInfoReturnable<Optional<PlayerPublicKey>> cir) {
        if (Config.getEnabled(NoChatSignatures.class))
            cir.setReturnValue(Optional.empty());
    }

    @Inject(method = "getPublicKey", at = @At("HEAD"), cancellable = true)
    private void onGetPublicKeyData(CallbackInfoReturnable<Optional<PlayerPublicKey.PublicKeyData>> cir) {
        if (Config.getEnabled(NoChatSignatures.class))
            cir.setReturnValue(Optional.empty());
    }
}
