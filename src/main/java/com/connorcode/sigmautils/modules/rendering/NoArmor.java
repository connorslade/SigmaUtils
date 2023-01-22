package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.list.EntityList;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public class NoArmor extends Module {
    static EnumSetting<Util.FilterType> filterType = Util.filterSetting(NoArmor.class).build();
    static DynamicListSetting<EntityType<?>> entities =
            new DynamicListSetting<>(NoArmor.class, "Entities", EntityList::new).description(
                            "Entities to disable armor rendering on. Whitelisting an entities will make it not render armor.")
                    .build();

    public NoArmor() {
        super("no_armor", "No Armor", "Dont render armor. Its that simple.", Category.Rendering);
    }

    public static boolean isRenderingArmor(LivingEntity entity) {
        var inList = entities.value().contains(entity.getType());
        return filterType.value() == Util.FilterType.Whitelist != inList;
    }
}
