package com.connorcode.sigmautils.event;

public interface CancellableI extends Event {
    boolean isCancelled();

    void setCancelled(boolean cancelled);

    default void cancel() {
        setCancelled(true);
    }
}