package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoChatSignatures extends Module {
    public NoChatSignatures() {
        super("no_chat_signatures", "No Chat Signatures", "Removes the signatures sent with chat messages from (1.19+)",
                Category.Chat);
    }
}
