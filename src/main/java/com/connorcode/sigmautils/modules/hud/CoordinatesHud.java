package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Objects;

public class CoordinatesHud extends HudModule {
    private static final NumberSetting precision = new NumberSetting(CoordinatesHud.class, "Precision", 0, 5).description("The precision of the coordinates displayed")
            .precision(0)
            .build();

    public CoordinatesHud() {
        super("coordinates_hud", "Coordinates Hud", "Adds a display on your screen showing your current location", Category.Hud);
        this.defaultTextColor = TextStyle.Color.Yellow;
        this.defaultOrder = 4;
    }

    @Override
    public String line() {
        int p = precision.intValue();
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        if (p == 0) return String.format("§r%sPos: §f%d, %d, %d", this.getTextColor(), (int) Math.round(player.getX()), (int) Math.round(player.getY()), (int) Math.round(player.getZ()));
        else return String.format("§r%sPos: §f%." + p + "f, %." + p + "f, %." + p + "f", this.getTextColor(), player.getX(), player.getY(), player.getZ());
    }
}
