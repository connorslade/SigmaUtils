package com.connorcode.sigmautils.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(YggdrasilAuthenticationService.class)
public interface YggdrasilAuthenticationServiceAccessor {
    @Accessor(remap = false)
    String getClientToken();
}
