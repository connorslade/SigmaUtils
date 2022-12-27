package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoChatNormalization extends Module {
    public NoChatNormalization() {
        super("no_chat_normalization", "No Chat Normalization", "Bypasses the chat message normalization.",
                Category.Chat);
    }
}
