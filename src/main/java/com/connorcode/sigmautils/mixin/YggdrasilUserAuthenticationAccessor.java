package com.connorcode.sigmautils.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(YggdrasilUserAuthentication.class)
public interface YggdrasilUserAuthenticationAccessor {
    @Invoker
    boolean invokeCheckTokenValidity();
}
