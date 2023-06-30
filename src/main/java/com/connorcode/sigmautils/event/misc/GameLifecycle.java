package com.connorcode.sigmautils.event.misc;

import com.connorcode.sigmautils.event.Event;

public class GameLifecycle {
    public static class WorldCloseEvent implements Event {}

    public static class ClientStartingEvent implements Event {}

    public static class ClientStoppingEvent implements Event {}
}
