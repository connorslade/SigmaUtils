package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.util.WorldUtils;
import com.connorcode.sigmautils.modules._interface.ChatPosition;
import com.connorcode.sigmautils.modules._interface.HotbarPosition;
import com.connorcode.sigmautils.modules._interface.NoScoreboardValue;
import com.connorcode.sigmautils.modules.hud.Hud;
import com.connorcode.sigmautils.modules.misc.BlockDistance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    void onRenderCrosshair(DrawContext ctx, RenderTickCounter renderTickCounter, CallbackInfo ci) {
        if (!Config.getEnabled(BlockDistance.class)) return;

        client.getProfiler().push("SigmaUtils::BlockDistance");
        var tickDelta = renderTickCounter.getTickDelta(true);
        var hitResult = WorldUtils.raycast(Objects.requireNonNull(client.getCameraEntity()), BlockDistance.maxDistance.value(), tickDelta);
        if (Objects.requireNonNull(hitResult).getType() == HitResult.Type.MISS) return;

        var distance = Objects.requireNonNull(hitResult).getPos().distanceTo(Objects.requireNonNull(client.player).getPos());

        var text = String.format("§f[%d]", (int) distance);
        var window = client.getWindow();
        ctx.drawText(client.textRenderer,
                     Text.of(text).asOrderedText(),
                     window.getScaledWidth() / 2 - client.textRenderer.getWidth(text) / 2,
                     window.getScaledHeight() / 2 + client.textRenderer.fontHeight,
                     0,
                     true);
        client.getProfiler().pop();
    }


    @Inject(method = "render", at = @At("TAIL"))
    void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (client.options.hudHidden || !Config.getEnabled(Hud.class)) return;
        Hud.renderHud(context);
    }


    @Redirect(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I"))
    int onGetWidth(TextRenderer instance, String text) {
        if (Config.getEnabled(NoScoreboardValue.class)) return 0;
        return instance.getWidth(text);
    }


    @Redirect(method = "method_55439(Lnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/number/NumberFormat;Lnet/minecraft/scoreboard/ScoreboardEntry;)Lnet/minecraft/client/gui/hud/InGameHud$SidebarEntry;", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardEntry;formatted(Lnet/minecraft/scoreboard/number/NumberFormat;)Lnet/minecraft/text/MutableText;"))
    MutableText onToString(ScoreboardEntry instance, NumberFormat format) {
        if (Config.getEnabled(NoScoreboardValue.class)) return Text.empty();
        return instance.formatted(format);
    }

    @Redirect(method = "renderChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V"))
    void onRenderChat(ChatHud instance, DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        if (Config.getEnabled(ChatPosition.class))
            matrices.translate(0, -ChatPosition.yPosition.intValue() * client.textRenderer.fontHeight, 0);
        instance.render(context, currentTick, mouseX, mouseY, focused);
        matrices.pop();
    }

    @Unique
    int getHotbarPos() {
        return Config.getEnabled(HotbarPosition.class) ? HotbarPosition.yPosition.intValue() : 0;
    }

    // Modified from https://github.com/yurisuika/Raise
    @ModifyArg(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"), index = 2)
    private int modifyHotbar(int value) {
        return value - getHotbarPos();
    }

    @ModifyArg(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"), index = 2)
    private int modifyItem(int value) {
        return value - getHotbarPos();
    }

    @ModifyVariable(method = "renderMountJumpBar", at = @At(value = "STORE"), ordinal = 1)
    private int modifyJumpBar(int value) {
        return value - getHotbarPos();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"), index = 2)
    private int modifyExperienceBarBackground(int value) {
        return value - getHotbarPos();
    }

    @ModifyArg(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIIIIIII)V"), index = 6)
    private int modifyExperienceBar(int value) {
        return value - getHotbarPos();
    }

    @ModifyArg(method = "renderExperienceLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"), index = 3)
    private int modifyXpText(int value) {
        return value - getHotbarPos();
    }

    @ModifyVariable(method = "renderHeldItemTooltip", at = @At(value = "STORE"), ordinal = 2)
    private int modifyHeldItemTooltip(int value) {
        return value - getHotbarPos();
    }

    @ModifyVariable(method = "renderStatusBars", at = @At(value = "STORE"), ordinal = 5)
    private int modifyStatusBars(int value) {
        return value - getHotbarPos();
    }

    @ModifyVariable(method = "renderMountHealth", at = @At(value = "STORE"), ordinal = 2)
    private int modifyMountHealth(int value) {
        return value - getHotbarPos();
    }

    @ModifyArg(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0), index = 1)
    private float modifyActionbar(float value) {
        return value - getHotbarPos();
    }
    // End
}
