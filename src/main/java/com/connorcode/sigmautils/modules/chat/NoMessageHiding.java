package com.connorcode.sigmautils.modules.chat;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoMessageHiding extends Module {
    public NoMessageHiding() {
        super("no_message_hiding", "No Message Hiding",
                "Removes the ability of the server to hide previously sent chat messages.", Category.Chat);
    }
}
