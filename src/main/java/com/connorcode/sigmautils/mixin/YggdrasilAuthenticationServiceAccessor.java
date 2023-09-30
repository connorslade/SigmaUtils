package com.connorcode.sigmautils.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(YggdrasilAuthenticationService.class)
public interface YggdrasilAuthenticationServiceAccessor {
//    @Accessor(remap = false)
//    String getClientToken();
}
