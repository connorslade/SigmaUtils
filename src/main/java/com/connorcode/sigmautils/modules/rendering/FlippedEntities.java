package com.connorcode.sigmautils.modules.rendering;


import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.list.EntityList;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.render.EntityRender;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.math.RotationAxis;

@ModuleInfo(description = "Always render entities upside down!")
public class FlippedEntities extends Module {
    static EnumSetting<Util.FilterType> filterType = Util.filterSetting(FlippedEntities.class).build();
    static DynamicListSetting<EntityType<?>> entities =
            new DynamicListSetting<>(FlippedEntities.class, "Entities", EntityList::new).description(
                            "Entities to render flipped. Whitelisting an entities will make it not render flipped.")
                    .build();

    public static boolean isFlipped(LivingEntity entity) {
        var inList = entities.value().contains(entity.getType());
        return filterType.value() == Util.FilterType.Whitelist == inList;
    }

    @EventHandler
    <E extends Entity> void onEntityRender(EntityRender.EntityRenderEvent<E> event) {
        var entity = event.getEntity();
        if (!enabled || entity instanceof LivingEntity) return;
        var entityType = entity.getType();
        var inList = entities.value().contains(entityType);
        if (!(filterType.value() == Util.FilterType.Whitelist == inList)) return;

        var matrices = event.getMatrices();
        if (entity instanceof ItemFrameEntity) matrices.translate(0.0F, 0.5F, 0.0F);
        else if (entity instanceof ItemEntity) matrices.translate(0.0F, 0.5F, 0.0F);
        else matrices.translate(0.0F, entity.getHeight() + 0.1F, 0.0F);

        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
    }
}
