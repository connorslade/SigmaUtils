package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class GlowingPlayers extends Module {
    public static BoolSetting disableF1 = new BoolSetting(GlowingPlayers.class, "Disable F1").value(true)
            .description("Turns off when in F1 mode")
            .build();

    public GlowingPlayers() {
        super("glowing_players", "Glowing Players", "Makes all players glow!", Category.Rendering);
    }

    public static boolean renderGlowing(Entity entity) {
        return entity instanceof PlayerEntity;
    }

    /*
    Entity Groups:
        - Player
        - Mob
        - Passive
        - Projectile
        - Vehicle
        - Item
        - Misc
     */
}
