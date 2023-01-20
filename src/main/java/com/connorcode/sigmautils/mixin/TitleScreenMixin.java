package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.ConfigGui;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.modules._interface.EscapeExit;
import com.connorcode.sigmautils.modules._interface.Minceraft;
import com.connorcode.sigmautils.modules._interface.SplashRefresh;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    long updateTime;
    @Shadow
    @Nullable
    private String splashText;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    // Always Enabled
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    void init(CallbackInfo ci) {
        ScreenAccessor screen = ((ScreenAccessor) this);
        Util.addDrawable(this,
                new ButtonWidget(screen.getWidth() / 2 - 100 - 24, screen.getHeight() / 4 + 48 + 24, 20, 20,
                        Text.of("Σ"), button -> MinecraftClient.getInstance()
                        .setScreen(new ConfigGui()),
                        ((button, matrices, mouseX, mouseY) -> screen.invokeRenderOrderedTooltip(matrices,
                                Language.getInstance()
                                        .reorder(List.of(Text.of("Sigma Utils"))), mouseX, mouseY))));
    }


    // For splash_refresh
    @Inject(method = "render", at = @At("HEAD"))
    void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        long now = System.currentTimeMillis();
        if (!Config.getEnabled(SplashRefresh.class) || now - updateTime < SplashRefresh.refreshTime.intValue() * 1000L)
            return;

        updateTime = now;
        assert this.client != null;
        this.splashText = this.client.getSplashTextLoader()
                .get();
    }

    // For minceraft
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/TitleScreen;isMinceraft:Z", opcode = Opcodes.GETFIELD))
    boolean isMinecraft(TitleScreen instance) {
        return Config.getEnabled(Minceraft.class);
    }

    // For escape_exit
    @Inject(method = "shouldCloseOnEsc", at = @At("HEAD"))
    void shouldCloseOnEsc(CallbackInfoReturnable<Boolean> cir) {
        if (Config.getEnabled(EscapeExit.class)) Objects.requireNonNull(this.client)
                .scheduleStop();
    }
}
