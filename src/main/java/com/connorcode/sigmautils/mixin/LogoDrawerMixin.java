package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.Minceraft;
import net.minecraft.client.gui.LogoDrawer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LogoDrawer.class)
public class LogoDrawerMixin {
    // For minceraft
    @Redirect(method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/LogoDrawer;minceraft:Z"))
    boolean isMinecraft(LogoDrawer instance) {
        return Config.getEnabled(Minceraft.class);
    }
}
