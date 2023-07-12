package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.config.settings.list.EntityList;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

@ModuleInfo(description = "Render all invisible entities")
public class ShowInvisibleEntities extends Module {
    public static NumberSetting opacity = new NumberSetting(ShowInvisibleEntities.class, "Opacity", 0, 1).value(0.25)
            .description("Opacity of invisible entities.")
            .build();
    static EnumSetting<Util.FilterType> filterType = Util.filterSetting(ShowInvisibleEntities.class).build();
    static DynamicListSetting<EntityType<?>> entities =
            new DynamicListSetting<>(ShowInvisibleEntities.class, "Entities", EntityList::new).description(
                            "Entities show while invisible. Whitelisting an entities will make it not render flipped.")
                    .build();

    public static boolean isInvisible(Entity entity) {
        var inList = entities.value().contains(entity.getType());
        return filterType.value() == Util.FilterType.Whitelist == inList;
    }
}
