package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.ChatPosition;
import com.connorcode.sigmautils.modules.chat.EggChat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;normalize(Ljava/lang/String;)Ljava/lang/String;"))
    String onChatMessageNormalize(ChatScreen instance, String chatText) {
        if (!chatText.isEmpty()) MinecraftClient.getInstance().inGameHud.getChatHud()
                .addToMessageHistory(chatText);

        String text = chatText;
        if (!Config.getEnabled("no_chat_normalization")) text = instance.normalize(chatText);
        if (Config.getEnabled("egg_chat") && !text.startsWith("/")) text = EggChat.eggify(text);
        return text;
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private int mouseYValueRender(int value) {
        return value + mouseYModifier();
    }

    @ModifyVariable(method = "mouseClicked", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double mouseYValueClick(double value) {
        return value + mouseYModifier();
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;renderTextHoverEffect(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Style;II)V"), index = 3)
    int onRenderTextHover(int value) {
        return value - mouseYModifier();
    }

    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addToMessageHistory(Ljava/lang/String;)V"))
    void onAddToMessageHistory(ChatHud instance, String message) {

    }

    int mouseYModifier() {
        if (Config.getEnabled("chat_position"))
            return ChatPosition.yPosition * MinecraftClient.getInstance().textRenderer.fontHeight;
        return 0;
    }
}
