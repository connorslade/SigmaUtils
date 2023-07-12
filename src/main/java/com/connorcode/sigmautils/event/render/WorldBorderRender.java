package com.connorcode.sigmautils.event.render;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.client.render.Camera;

public class WorldBorderRender extends Cancellable {
    Camera camera;

    public WorldBorderRender(Camera camera) {
        this.camera = camera;
    }
}
