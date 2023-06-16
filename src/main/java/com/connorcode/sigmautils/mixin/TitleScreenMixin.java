package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.ConfigGui;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.modules._interface.EscapeExit;
import com.connorcode.sigmautils.modules._interface.SplashRefresh;
import com.connorcode.sigmautils.modules._interface.UiTweaks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Shadow
    @Nullable
    private SplashTextRenderer splashText;
    long updateTime;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    // Always Enabled
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    void init(CallbackInfo ci) {
        ScreenAccessor screen = ((ScreenAccessor) this);
        var y = screen.getHeight() / 4 + 48 + 24;
        if (UiTweaks.noRealms()) y += 24 * 2 - 20;
        Util.addChild(this,
                ButtonWidget.builder(Text.of("Σ"), button -> Objects.requireNonNull(client).setScreen(new ConfigGui()))
                        .position(screen.getWidth() / 2 - 100 - 24, y)
                        .size(20, 20)
                        .tooltip(Tooltip.of(Text.of("Sigma Utils")))
                        .build());
    }


    // For splash_refresh
    @Inject(method = "render", at = @At("HEAD"))
    void onRender(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        long now = System.currentTimeMillis();
        if (!Config.getEnabled(SplashRefresh.class) || now - updateTime < SplashRefresh.refreshTime.intValue() * 1000L)
            return;

        updateTime = now;
        assert this.client != null;
        this.splashText = this.client.getSplashTextLoader().get();
    }

    // For escape_exit
    @Inject(method = "shouldCloseOnEsc", at = @At("HEAD"))
    void shouldCloseOnEsc(CallbackInfoReturnable<Boolean> cir) {
        if (Config.getEnabled(EscapeExit.class))
            Objects.requireNonNull(this.client).scheduleStop();
    }

    @Inject(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 2, shift = At.Shift.BEFORE), cancellable = true)
    void onInitRealmsWidget(int y, int spacingY, CallbackInfo ci) {
        if (UiTweaks.noRealms()) ci.cancel();
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;initWidgetsNormal(II)V"))
    void onInit(TitleScreen instance, int y, int spacingY) {
        if (UiTweaks.noRealms()) y += spacingY * 2 - 20;
        ((TitleScreenAccessor) instance).invokeInitWidgetsNormal(y, spacingY);
    }
}
