package com.connorcode.sigmautils.event.render;

import com.connorcode.sigmautils.event.CancellableI;
import com.connorcode.sigmautils.event.Event;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class EntityRender {
    public static class EntityPreRenderEvent<E extends Entity> extends EntityRenderEventI<E> implements CancellableI {
        boolean cancelled = false;

        public EntityPreRenderEvent(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            super(entity, x, y, z, yaw, tickDelta, matrices, vertexConsumers, light);
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

    public static class EntityRenderEvent<E extends Entity> extends EntityRenderEventI<E> {
        public EntityRenderEvent(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            super(entity, x, y, z, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

    public static class EntityPostRenderEvent<E extends Entity> extends EntityRenderEventI<E> {
        public EntityPostRenderEvent(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            super(entity, x, y, z, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

    public static abstract class EntityRenderEventI<E extends Entity> implements Event {
        E entity;
        double x, y, z;
        double yaw;
        double tickDelta;
        MatrixStack matrices;
        VertexConsumerProvider vertexConsumers;
        int light;

        public EntityRenderEventI(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            this.entity = entity;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.tickDelta = tickDelta;
            this.matrices = matrices;
            this.vertexConsumers = vertexConsumers;
            this.light = light;
        }

        public E getEntity() {
            return entity;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public double getYaw() {
            return yaw;
        }

        public double getTickDelta() {
            return tickDelta;
        }

        public MatrixStack getMatrices() {
            return matrices;
        }

        public VertexConsumerProvider getVertexConsumers() {
            return vertexConsumers;
        }

        public int getLight() {
            return light;
        }
    }
}
