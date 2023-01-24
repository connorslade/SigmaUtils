package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static net.minecraft.world.World.NETHER;
import static net.minecraft.world.World.OVERWORLD;

public class CoordinatesHud extends HudModule {
    private static final NumberSetting precision =
            new NumberSetting(CoordinatesHud.class, "Precision", 0, 5).description(
                            "The precision of the coordinates displayed")
                    .precision(0)
                    .build();
    private static final BoolSetting nether = new BoolSetting(CoordinatesHud.class, "Nether Coordinates").value(false)
            .description("When in the overworld or nether, display the coordinates of the other dimension")
            .build();
    private static final EnumSetting<TextStyle.Color> netherStyle =
            new EnumSetting<>(CoordinatesHud.class, "Nether Color", TextStyle.Color.class).value(TextStyle.Color.Red)
                    .description("The color of the text")
                    .category("Hud")
                    .build();

    public CoordinatesHud() {
        super("coordinates_hud", "Coordinates Hud", "Adds a display on your screen showing your current location",
                Category.Hud);
        this.defaultTextColor = TextStyle.Color.Yellow;
        this.defaultOrder = 4;
    }

    private String formatPos(Vec3d pos) {
        int p = precision.intValue();
        return String.format("%." + p + "f, %." + p + "f, %." + p + "f", pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public String line() {
        ClientPlayerEntity player = Objects.requireNonNull(client.player);
        RegistryKey<World> registryKey = player.world.getRegistryKey();

        Vec3d otherPos = null;
        if (registryKey == OVERWORLD) otherPos = player.getPos()
                .multiply(0.125, 1, 0.125);
        else if (registryKey == NETHER) otherPos = player.getPos()
                .multiply(8, 1, 8);
        return String.format("§r%sPos: §f%s%s", this.getTextColor(), formatPos(player.getPos()),
                nether.value() && otherPos != null ? String.format(" %s[%s]", netherStyle.value().code(),
                        formatPos(otherPos)) : "");
    }
}
