package com.connorcode.sigmautils.misc.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtils {
    public static Vec3d entityPos(Entity entity, float tickDelta) {
        return entityPos(new Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ), new Vec3d(entity.getX(), entity.getY(), entity.getZ()), tickDelta);
    }

    public static Vec3d entityPos(Vec3d old, Vec3d current, float tickDelta) {
        var x = MathHelper.lerp(tickDelta, old.getX(), current.getX());
        var y = MathHelper.lerp(tickDelta, old.getY(), current.getY());
        var z = MathHelper.lerp(tickDelta, old.getZ(), current.getZ());
        return new Vec3d(x, y, z);
    }
}
