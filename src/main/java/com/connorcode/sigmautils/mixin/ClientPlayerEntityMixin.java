package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.PrintDeathCords;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Objects;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "requestRespawn", at = @At("TAIL"))
    public void onRequestRespawn(CallbackInfo ci) throws Exception {
        if (!Config.getEnabled("print_death_cords") || PrintDeathCords.lastDeath == null) return;

        long[] lastDeath = Arrays.stream(PrintDeathCords.lastDeath)
                .mapToLong(Math::round)
                .toArray();
        Objects.requireNonNull(MinecraftClient.getInstance().player)
                .sendMessage(
                        Text.of(String.format("§6[Σ] Last death: %d, %d, %d", lastDeath[0], lastDeath[1],
                                lastDeath[2])));
        PrintDeathCords.lastDeath = null;
    }
}
