package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.network.ServerInfo;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class ServerHud extends HudModule {
    private static final BoolSetting serverType = new BoolSetting(ServerHud.class, "Server Type").value(true)
            .description("Show Server Type")
            .build();
    private static final EnumSetting<TextStyle.Color> serverTypeStyle =
            new EnumSetting<>(ServerHud.class, "Server type color", TextStyle.Color.class).value(TextStyle.Color.Red)
                    .description("The color of the text of the server type")
                    .category("Hud");

    public ServerHud() {
        super("server_hud", "Server Hud", "Shows the server you are currently on in the HUD", Category.Hud);
        this.defaultTextColor = TextStyle.Color.Gold;
        this.defaultOrder = 7;
    }

    @Override
    public void init() {
        super.init();
        serverTypeStyle.build();
    }

    public String line() {
        ServerInfo serverEntry = client.getCurrentServerEntry();

        return String.format("§r%sServer: §f%s%s", this.getTextColor(),
                serverEntry == null ? "Integrated Server" : serverEntry.address,
                (serverType.value() && client.player != null) ? String.format(" %s[%s]", serverTypeStyle.value().code(),
                        client.player.getServerBrand()) : "");
    }
}
