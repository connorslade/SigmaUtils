package com.connorcode.sigmautils.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Shadow
    protected abstract boolean isChatFocused();

//    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;isChatFocused()Z"))
//    boolean isChatFocused(ChatHud instance) {
//        if (Config.getEnabled(NoChatFade.class))
//            return true;
//        return isChatFocused();
//    }
//
//    @Inject(method = "removeMessage", at = @At("HEAD"), cancellable = true)
//    void onHideMessage(MessageSignatureData signature, CallbackInfo ci) {
//        if (Config.getEnabled(NoMessageHiding.class)) ci.cancel();
//    }
}
