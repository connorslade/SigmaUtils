package com.connorcode.sigmautils.config.settings.list;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

public class EntityList extends SimpleList<EntityType<?>> {

    public EntityList(DynamicListSetting<EntityType<?>> setting) {
        super(setting, Registry.ENTITY_TYPE);
    }

    @Override
    public String getDisplay(EntityType<?> value) {
        return value.getName().getString();
    }
}
