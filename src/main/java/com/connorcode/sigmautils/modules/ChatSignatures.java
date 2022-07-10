package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.Category;
import com.connorcode.sigmautils.Module;
import net.minecraft.nbt.NbtCompound;

public class ChatSignatures extends Module {
    public ChatSignatures() {
        super("no_chat_signatures", "No Chat Signatures",
                "Disables sending the chat signatures used for chat reporting", Category.Misc);
    }

    public void loadConfig(NbtCompound config) {
        enabled = config.getBoolean("enabled");
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("enabled", enabled);
        return nbt;
    }
}
