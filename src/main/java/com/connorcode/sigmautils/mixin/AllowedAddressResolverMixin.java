package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.AllowBlockedServers;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.BlockListChecker;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AllowedAddressResolver.class)
public class AllowedAddressResolverMixin {
    // For allow_blocked_servers
    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/BlockListChecker;isAllowed(Lnet/minecraft/client/network/Address;)Z"))
    boolean onIsAllowed(BlockListChecker instance, Address address) {
        if (Config.getEnabled(AllowBlockedServers.class)) return true;
        return instance.isAllowed(address);
    }

    // For allow_blocked_servers
    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/BlockListChecker;isAllowed(Lnet/minecraft/client/network/ServerAddress;)Z"))
    boolean onIsAllowed(BlockListChecker instance, ServerAddress serverAddress) {
        if (Config.getEnabled(AllowBlockedServers.class)) return true;
        return instance.isAllowed(serverAddress);
    }
}
