package com.connorcode.sigmautils.event.render;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class EntityRender {
    public static class EntityRenderEvent<E extends Entity> extends Cancellable {
        E entity;
        double x, y, z;
        double yaw;
        double tickDelta;
        MatrixStack matrices;
        VertexConsumerProvider vertexConsumers;
        int light;

        public EntityRenderEvent(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
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
