package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Datatypes;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.nbt.NbtCompound;

public class ChatPosition extends Module {
    public static NumberSetting yPosition = new NumberSetting(ChatPosition.class, "Y Position", 0, 10).precision(0)
            .description("The number of lines to offset the chat display by")
            .build();

    public ChatPosition() {
        super("chat_position", "Chat Position", "Lets you move the chat panel up", Category.Interface);
    }

    public void init() {
        ClientCommandRegistrationCallback.EVENT.register(
                ((dispatcher, registryAccess) -> Util.moduleConfigCommand(dispatcher, this, "yPosition",
                        Datatypes.Integer, context -> {
//                            yPosition = getInteger(context, "setting");
                            return 0;
                        })));
    }

    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
//        yPosition = config.getInt("y_position");
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = Util.saveEnabled(enabled);
//        nbt.putInt("y_position", yPosition);
        return nbt;
    }
}
