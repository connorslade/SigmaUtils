package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.module.ModuleInfo;
import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.list.EntityList;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

@ModuleInfo(description = "Disables all entity shadows")
public class DisableShadows extends Module {
    static EnumSetting<Util.FilterType> filterType = Util.filterSetting(DisableShadows.class).build();
    static DynamicListSetting<EntityType<?>> entities =
            new DynamicListSetting<>(DisableShadows.class, "Entities", EntityList::new).description(
                            "Entities to disable shadows for. Whitelisting an entities will make it not render shadows.")
                    .build();

    public static boolean isDisabled(Entity entity) {
        var inList = entities.value().contains(entity.getType());
        return filterType.value() == Util.FilterType.Whitelist == inList;
    }
}
