package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.PrintDeathCords;
import com.mojang.brigadier.ParseResults;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Objects;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "requestRespawn", at = @At("TAIL"))
    public void onRequestRespawn(CallbackInfo ci) {
        if (!Config.getEnabled("print_death_cords") || PrintDeathCords.lastDeath == null) return;

        long[] lastDeath = Arrays.stream(PrintDeathCords.lastDeath)
                .mapToLong(Math::round)
                .toArray();
        Objects.requireNonNull(MinecraftClient.getInstance().player)
                .sendMessage(
                        Text.of(String.format("§6Σ] Last death: %d, %d, %d", lastDeath[0], lastDeath[1],
                                lastDeath[2])));
        PrintDeathCords.lastDeath = null;
    }

    @Redirect(method = "updateNausea", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;closeHandledScreen()V"))
    void onCloseHandledScreen(ClientPlayerEntity instance) {
        if (!Config.getEnabled("portal_inventory")) instance.closeHandledScreen();
    }

    @Redirect(method = "updateNausea", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    void onSetScreen(MinecraftClient instance, Screen screen) {
        if (!Config.getEnabled("portal_inventory")) instance.setScreen(screen);
    }

    @Inject(method = "signChatMessage", at = @At("HEAD"), cancellable = true)
    void onSignChatMessage(MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<MessageSignatureData> cir) {
        if (Config.getEnabled("no_chat_signatures")) cir.setReturnValue(MessageSignatureData.EMPTY);
    }

    @Inject(method = "signArguments", at = @At("HEAD"), cancellable = true)
    void onSignArguments(MessageMetadata signer, ParseResults<CommandSource> parseResults, Text preview, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
        if (Config.getEnabled("no_chat_signatures")) cir.setReturnValue(ArgumentSignatureDataMap.EMPTY);
    }
}
