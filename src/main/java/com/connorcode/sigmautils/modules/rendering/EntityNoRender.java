package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.list.EntityList;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.render.EntityRender;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public class EntityNoRender extends Module {
    static EnumSetting<Util.FilterType> filterType = Util.filterSetting(EntityNoRender.class).build();
    static DynamicListSetting<EntityType<?>> entities =
            new DynamicListSetting<>(EntityNoRender.class, "Entities", EntityList::new).description(
                            "Entities to disable rendering for. Whitelisting an entities will make it not render.")
                    .build();

    public EntityNoRender() {
        super("entity_no_render", "Entity No Render", "Prevents rendering of entities", Category.Rendering);
    }

    @EventHandler
    <E extends Entity> void onEntityRender(EntityRender.EntityRenderEvent<E> event) {
        if (!enabled) return;
        var entityType = event.getEntity().getType();
        var inList = entities.value().contains(entityType);
        if (filterType.value() == Util.FilterType.Whitelist == inList)
            event.cancel();
    }
}

// TODO: test if enentiy modules work on non lving eneities