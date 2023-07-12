package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.ModuleInfo;
import com.connorcode.sigmautils.config.settings.DynamicSelectorSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.config.settings.list.SimpleSelector;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Automatically cycles a villager's trades until you get the desired trade. [EXPERIMENTAL]")
public class AutoTradeCycle extends Module {
    DynamicSelectorSetting<Enchantment> enchantment =
            new DynamicSelectorSetting<>(AutoTradeCycle.class, "Enchantment", EnchantmentList::new)
                    .description("The enchantments that will stop the cycling.")
                    .build();
    NumberSetting enchantmentLevel =
            new NumberSetting(AutoTradeCycle.class, "Enchantment Level", 1, 1)
                    .value(1)
                    .precision(0)
                    .description("The level of the enchantment that will stop the cycling.")
                    .build();

    class EnchantmentList extends SimpleSelector<Enchantment> {

        public EnchantmentList(DynamicSelectorSetting<Enchantment> setting) {
            super(setting, Registries.ENCHANTMENT);
        }

        @Override
        public String getDisplay(Enchantment resource) {
            if (resource == null) return "None";
            var val = resource.getName(1).getString();
            if (val.endsWith(" I")) val = val.substring(0, val.length() - 2);
            return val;
        }

        @Override
        public void onChange(@Nullable Enchantment resource) {
            if (resource == null) {
                AutoTradeCycle.this.enchantmentLevel.min(1).max(1).value(1);
                return;
            }
            AutoTradeCycle.this.enchantmentLevel.min(resource.getMinLevel())
                    .max(resource.getMaxLevel())
                    .value(resource.getMaxLevel());
            if (client.currentScreen != null) ((ScreenAccessor) client.currentScreen).invokeClearAndInit();
        }
    }
}
