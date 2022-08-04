package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoChatNormalization extends BasicModule {
    public NoChatNormalization() {
        super("no_chat_normalization", "No Chat Normalization", "Bypasses the chat message normalization.",
                Category.Chat);
    }
}
