package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class ChatSignatures extends BasicModule {
    public ChatSignatures() {
        super("no_chat_signatures", "No Chat Signatures",
                "Disables sending the chat signatures used for chat reporting", Category.Misc);
    }
}
