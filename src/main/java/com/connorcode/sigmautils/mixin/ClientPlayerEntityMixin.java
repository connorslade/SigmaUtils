package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.PortalInventory;
import com.connorcode.sigmautils.modules.misc.PrintDeathCords;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "requestRespawn", at = @At("TAIL"))
    public void onRequestRespawn(CallbackInfo ci) {
        if (!Config.getEnabled(PrintDeathCords.class) || PrintDeathCords.lastDeath == null)
            return;

        long[] lastDeath = Arrays.stream(PrintDeathCords.lastDeath).mapToLong(Math::round).toArray();
        Objects.requireNonNull(client.player)
                .sendMessage(Text.of(String.format("§6Σ] Last death: %d, %d, %d", lastDeath[0], lastDeath[1],
                        lastDeath[2])));
        PrintDeathCords.lastDeath = null;
    }

    @Redirect(method = "updateNausea", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;closeHandledScreen()V"))
    void onCloseHandledScreen(ClientPlayerEntity instance) {
        if (!Config.getEnabled(PortalInventory.class))
            instance.closeHandledScreen();
    }

    @Redirect(method = "updateNausea", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    void onSetScreen(MinecraftClient instance, Screen screen) {
        if (!Config.getEnabled(PortalInventory.class))
            instance.setScreen(screen);
    }
}
