package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.list.EntityList;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public class FlippedEntities extends Module {
    static EnumSetting<Util.FilterType> filterType = Util.filterSetting(FlippedEntities.class).build();
    static DynamicListSetting<EntityType<?>> entities =
            new DynamicListSetting<>(FlippedEntities.class, "Entities", EntityList::new).description(
                            "Entities to render flipped. Whitelisting an entities will make it not render flipped.")
                    .build();

    public FlippedEntities() {
        super("flipped_entities", "Flipped Entities", "Always render entities upside down!", Category.Rendering);
    }

    public static boolean isFlipped(LivingEntity entity) {
        var inList = entities.value().contains(entity.getType());
        return filterType.value() == Util.FilterType.Whitelist == inList;
    }
}
