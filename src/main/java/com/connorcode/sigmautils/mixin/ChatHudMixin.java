package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Shadow protected abstract boolean isChatFocused();

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;isChatFocused()Z"))
    boolean isChatFocused(ChatHud instance) throws Exception {
        if (Config.getEnabled("no_chat_fade")) return true;
        return isChatFocused();
    }
}
