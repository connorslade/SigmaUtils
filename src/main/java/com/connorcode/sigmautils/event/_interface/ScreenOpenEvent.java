package com.connorcode.sigmautils.event._interface;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.client.gui.screen.Screen;

public class ScreenOpenEvent extends Cancellable {
    public Screen screen;

    public ScreenOpenEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen get() {
        return screen;
    }
}