package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoChatSignatures extends BasicModule {
    public NoChatSignatures() {
        super("no_chat_signatures", "No Chat Signatures", "Removes the signatures sent with chat messages from (1.19+)",
                Category.Chat);
    }
}
