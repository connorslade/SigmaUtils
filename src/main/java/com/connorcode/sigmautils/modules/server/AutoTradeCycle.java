package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.ModuleInfo;
import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.list.SimpleList;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;

@ModuleInfo(description = "Automatically cycles a villager's trades until you get the desired trade. [EXPERIMENTAL]")
public class AutoTradeCycle extends Module {
    DynamicListSetting<Enchantment> enchantments =
            new DynamicListSetting<>(AutoTradeCycle.class, "Enchantments", EnchantmentList::new)
                    .description("The enchantments that will stop the cycling.")
                    .build();

    static class EnchantmentList extends SimpleList<Enchantment> {

        public EnchantmentList(DynamicListSetting<Enchantment> setting) {
            super(setting, Registries.ENCHANTMENT);
        }

        @Override
        public String getDisplay(Enchantment resource) {
            return resource.getName(1).getString();
        }
    }
}
