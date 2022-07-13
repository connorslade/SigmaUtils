package com.connorcode.sigmautils.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.RaycastContext;

public class Raycast {
    // Modified from https://fabricmc.net/wiki/tutorial:pixel_raycast
    public static HitResult raycastInDirection(MinecraftClient client, float tickDelta, double maxDistance, Vec3d direction) {
        Entity entity = client.getCameraEntity();
        if (entity == null || client.world == null) return null;

        HitResult target = raycast(entity, maxDistance, tickDelta, false, direction);
        Vec3d cameraPos = entity.getCameraPosVec(tickDelta);

        Vec3d vec3d3 = cameraPos.add(direction.multiply(maxDistance));
        Box box = entity.getBoundingBox()
                .stretch(entity.getRotationVec(1.0F)
                        .multiply(maxDistance))
                .expand(1.0D, 1.0D, 1.0D);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, cameraPos, vec3d3, box,
                (entityX) -> !entityX.isSpectator() && entityX.collides(), maxDistance);

        if (entityHitResult == null)
            return target;

        Entity entity2 = entityHitResult.getEntity();
        Vec3d vec3d4 = entityHitResult.getPos();
        double g = cameraPos.squaredDistanceTo(vec3d4);
        if (g < maxDistance || target == null) {
            target = entityHitResult;
            if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) {
                client.targetedEntity = entity2;
            }
        }

        return target;
    }

    public static Vec3d map(float anglePerPixel, Vec3d center, Vec3f horizontalRotationAxis, Vec3f verticalRotationAxis, int x, int y, int width, int height) {
        float horizontalRotation = (x - width / 2f) * anglePerPixel;
        float verticalRotation = (y - height / 2f) * anglePerPixel;

        final Vec3f temp2 = new Vec3f(center);
        temp2.rotate(verticalRotationAxis.getDegreesQuaternion(verticalRotation));
        temp2.rotate(horizontalRotationAxis.getDegreesQuaternion(horizontalRotation));
        return new Vec3d(temp2);
    }

    public static HitResult raycast(Entity entity, double maxDistance, float tickDelta, boolean includeFluids, Vec3d direction) {
        Vec3d end = entity.getCameraPosVec(tickDelta)
                .add(direction.multiply(maxDistance));
        return entity.world.raycast(
                new RaycastContext(entity.getCameraPosVec(tickDelta), end, RaycastContext.ShapeType.OUTLINE,
                        includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity));
    }
}
