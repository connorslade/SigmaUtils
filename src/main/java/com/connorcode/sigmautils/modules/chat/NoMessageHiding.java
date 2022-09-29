package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class NoMessageHiding extends BasicModule {
    public NoMessageHiding() {
        super("no_message_hiding", "No Message Hiding",
                "Removes the ability of the server to hide previously sent chat messages.", Category.Chat);
    }
}
