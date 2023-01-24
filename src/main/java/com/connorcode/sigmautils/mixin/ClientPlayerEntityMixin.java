package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.PortalInventory;
import com.connorcode.sigmautils.modules.chat.NoChatSignatures;
import com.connorcode.sigmautils.modules.misc.ForceFly;
import com.connorcode.sigmautils.modules.misc.PrintDeathCords;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.ParseResults;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.message.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Objects;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    @Final
    protected MinecraftClient client;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Redirect(method = "sendAbilitiesUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAbilities()Lnet/minecraft/entity/player/PlayerAbilities;"))
    PlayerAbilities onSendAbilitiesUpdate(ClientPlayerEntity instance) {
        PlayerAbilities abilities = new PlayerAbilities();
        abilities.flying = !Config.getEnabled(ForceFly.class) && instance.getAbilities().flying;
        return abilities;
    }

    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z", ordinal = 0, opcode = Opcodes.GETFIELD))
    boolean allowFlyingAllowed(PlayerAbilities instance) {
        return Config.getEnabled(ForceFly.class) || instance.allowFlying;
    }

    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z", ordinal = 1, opcode = Opcodes.GETFIELD))
    boolean allowFlying(PlayerAbilities instance) {
        return Config.getEnabled(ForceFly.class) || instance.allowFlying;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSwimming()Z", ordinal = 2))
    boolean allowFlyingWhenSwim(ClientPlayerEntity instance) {
        return !Config.getEnabled(ForceFly.class) && instance.isSwimming();
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

    @Inject(method = "signChatMessage", at = @At("HEAD"), cancellable = true)
    void onSignChatMessage(MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages,
                           CallbackInfoReturnable<MessageSignatureData> cir) {
        if (Config.getEnabled(NoChatSignatures.class))
            cir.setReturnValue(MessageSignatureData.EMPTY);
    }

    @Inject(method = "signArguments", at = @At("HEAD"), cancellable = true)
    void onSignArguments(MessageMetadata signer, ParseResults<CommandSource> parseResults, Text preview,
                         LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
        if (Config.getEnabled(NoChatSignatures.class))
            cir.setReturnValue(ArgumentSignatureDataMap.EMPTY);
    }
}
