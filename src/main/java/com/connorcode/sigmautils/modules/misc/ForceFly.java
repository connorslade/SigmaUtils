package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerAbilities;

public class ForceFly extends BasicModule {
    public static boolean oldValue = false;

    public ForceFly() {
        super("force_fly", "Force Allow Flying", "Forces the client to allow you to fly ingame.", Category.Misc);
    }

    @Override
    public void enable(MinecraftClient client) {
        if (client.player == null) return;
        PlayerAbilities abilities = client.player.getAbilities();
        oldValue = abilities.allowFlying;
        abilities.allowFlying = true;
        super.enable(client);
    }

    @Override
    public void disable(MinecraftClient client) {
        if (client.player == null) return;
        PlayerAbilities abilities = client.player.getAbilities();
        if (!oldValue && abilities.flying) abilities.flying = false;
        abilities.allowFlying = oldValue;
        super.disable(client);
    }
}
