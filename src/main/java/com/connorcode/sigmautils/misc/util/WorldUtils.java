package com.connorcode.sigmautils.misc.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;

public class WorldUtils {
    public static HitResult raycast(Entity entity, double maxDistance, float tickDelta) {
        var end = entity.getCameraPosVec(tickDelta).add(entity.getRotationVec(tickDelta).multiply(maxDistance));
        return entity.method_48926().raycast(
                new RaycastContext(entity.getCameraPosVec(tickDelta), end, RaycastContext.ShapeType.OUTLINE,
                                   RaycastContext.FluidHandling.NONE, entity));
    }
}
