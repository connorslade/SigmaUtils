package com.connorcode.sigmautils.config.settings.list;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;

public class EntityList extends SimpleList<EntityType<?>> {

    public EntityList(DynamicListSetting<EntityType<?>> setting) {
        super(setting, Registries.ENTITY_TYPE);
    }

    @Override
    public String getDisplay(EntityType<?> value) {
        return value.getName().getString();
    }

    @Override
    public String type() {
        return "Entity";
    }
}
