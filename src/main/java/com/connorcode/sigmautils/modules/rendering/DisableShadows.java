package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.list.EntityList;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public class DisableShadows extends Module {
    static EnumSetting<Util.FilterType> filterType = Util.filterSetting(DisableShadows.class).build();
    static DynamicListSetting<EntityType<?>> entities =
            new DynamicListSetting<>(DisableShadows.class, "Entities", EntityList::new).description(
                            "Entities to disable shadows for. Whitelisting an entities will make it not render shadows.")
                    .build();

    public DisableShadows() {
        super("disable_shadows", "Disable Shadows", "Disables all entity shadows", Category.Rendering);
    }

    public static boolean isDisabled(Entity entity) {
        var inList = entities.value().contains(entity.getType());
        return filterType.value() == Util.FilterType.Whitelist == inList;
    }
}
