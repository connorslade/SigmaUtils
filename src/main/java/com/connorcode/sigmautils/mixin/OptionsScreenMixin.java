package com.connorcode.sigmautils.mixin;


import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.UiTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    @Unique
    ButtonWidget muteButton;

    @Unique
    Widget soundButton;

    @Unique
    void positionButton() {
        muteButton.setX(soundButton.getX() + soundButton.getWidth() + 5);
        muteButton.setY(soundButton.getY());
    }

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 1))
    <T extends Widget> T onAddSoundButton(T widget) {
        if (!Config.getEnabled(UiTweaks.class) || !UiTweaks.audioMuteButton.value()) return widget;

        soundButton = widget;
        muteButton = addDrawableChild(ButtonWidget.builder(Text.of(UiTweaks.muted ? "U" : "M"), button -> {
            UiTweaks.muted ^= true;
            button.setMessage(Text.of(UiTweaks.muted ? "U" : "M"));
        }).size(20, 20).build());

        return widget;
    }

    @Inject(method = "init", at = @At("TAIL"))
    void onInitTail(CallbackInfo ci) {
        positionButton();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        positionButton();
    }
}
