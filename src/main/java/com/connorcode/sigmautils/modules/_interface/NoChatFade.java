package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoChatFade extends Module {
    public NoChatFade() {
        super("no_chat_fade", "No Chat Fade", "Removes the effect where messages fade out after 10 seconds",
                Category.Interface);
    }
}
