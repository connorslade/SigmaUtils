package com.connorcode.sigmautils.event;

public abstract class Cancellable implements CancellableI {
    boolean cancelled = false;

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
