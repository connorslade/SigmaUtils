package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.NoChatFade;
import com.connorcode.sigmautils.modules.chat.NoMessageHiding;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.network.message.MessageSignatureData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    boolean isFocused(boolean focused) {
        if (Config.getEnabled(NoChatFade.class)) return true;
        return focused;
    }

    @Inject(method = "removeMessage", at = @At("HEAD"), cancellable = true)
    void onHideMessage(MessageSignatureData signature, CallbackInfo ci) {
        if (Config.getEnabled(NoMessageHiding.class)) ci.cancel();
    }
}
