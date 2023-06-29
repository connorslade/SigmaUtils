package com.connorcode.sigmautils.event._interface;

import com.connorcode.sigmautils.event.Cancellable;

public class MouseScrollEvent extends Cancellable {
    public double horizontal;
    public double vertical;

    public MouseScrollEvent(double horizontal, double vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }
}