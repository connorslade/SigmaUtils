package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.modules._interface.UiTweaks;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.connorcode.sigmautils.SigmaUtils.client;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {

    @Shadow
    @Nullable
    private TextFieldWidget searchField;

    @Inject(method = "reset", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setText(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    void onSetText(CallbackInfo ci) {
        if (!UiTweaks.persistentCraftingBookSearch() || UiTweaks.craftingBookSearch.isEmpty()) return;
        assert this.searchField != null;
        this.searchField.setText(UiTweaks.craftingBookSearch);
    }

    @Inject(method = "initialize", at = @At("RETURN"))
    void onInit(CallbackInfo ci) {
        if (!UiTweaks.persistentCraftingBookSearch() || UiTweaks.craftingBookSearch.isEmpty()) return;
        if (this.searchField != null)
            this.searchField.setText(UiTweaks.craftingBookSearch);
        else this.searchField =
                new TextFieldWidget(client.textRenderer, 0, 0, 0, 0, Text.of(UiTweaks.craftingBookSearch));
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;refreshSearchResults()V", shift = At.Shift.BEFORE))
    void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.searchField != null) UiTweaks.craftingBookSearch = this.searchField.getText();
    }

    @Inject(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;refreshSearchResults()V", shift = At.Shift.BEFORE))
    void onCharTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.searchField != null) UiTweaks.craftingBookSearch = this.searchField.getText();
    }
}
