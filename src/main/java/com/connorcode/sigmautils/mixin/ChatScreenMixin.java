package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.EggChat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;normalize(Ljava/lang/String;)Ljava/lang/String;"))
    String onChatMessageNormalize(ChatScreen instance, String chatText) throws Exception {
        if (!chatText.isEmpty()) MinecraftClient.getInstance().inGameHud.getChatHud()
                .addToMessageHistory(chatText);

        String text = chatText;
        if (!Config.getEnabled("no_chat_normalization")) text = instance.normalize(chatText);
        if (Config.getEnabled("egg_chat") && !text.startsWith("/")) text = EggChat.eggify(text);
        return text;
    }

    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addToMessageHistory(Ljava/lang/String;)V"))
    void onAddToMessageHistory(ChatHud instance, String message) {

    }
}
