package com.connorcode.sigmautils.event.misc;

import com.connorcode.sigmautils.event.Cancellable;

public class Tick {
    public static class GameTickEvent extends Cancellable {
        public GameTickEvent() {}
    }

    public static class RenderTickEvent extends Cancellable {
        public RenderTickEvent() {}
    }
}
