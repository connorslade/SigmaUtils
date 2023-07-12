package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.DynamicSelectorSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.config.settings.list.SimpleSelector;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.misc.GameLifecycle;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.event.render.EntityRender;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.village.VillagerData;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

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
    NumberSetting villagerDistance =
            new NumberSetting(AutoVoidTrade.class, "Villager Distance", 0, 15).value(10)
                    .description("The distance from the player that the villager must be to be selected")
                    .precision(0)
                    .build();

    @Nullable
    VillagerEntity villager;

    @Override
    public void enable() {
        super.enable();
        if (client.world == null || client.player == null) {
            fail("No world loaded, disabling.");
            return;
        }

        var loadedVillagers = new HashSet<>(client.world.getEntitiesByClass(VillagerEntity.class,
                client.player.getBoundingBox().expand(villagerDistance.intValue()), villagerEntity -> true));
        if (loadedVillagers.size() > 1) fail("Multiple villagers in range, disabling");
        else if (loadedVillagers.isEmpty()) fail("No villagers in range, disabling");
        else this.villager = loadedVillagers.stream().findFirst().orElse(null);
    }

    @EventHandler
    void onWorldClose(GameLifecycle.WorldCloseEvent event) {
        villager = null;
    }

    @EventHandler
    void onPacketReceive(PacketReceiveEvent event) {
        if (!enabled || !(event.get() instanceof EntityTrackerUpdateS2CPacket trackerUpdate) || villager == null ||
                trackerUpdate.id() != villager.getId()) return;

        var trackedValues = trackerUpdate.trackedValues();
        if (trackedValues.isEmpty()) return;
        for (var trackedValue : trackedValues) {
            if (trackedValue.id() != 18 || !(trackedValue.value() instanceof VillagerData villagerData)) continue;
            if (villager.getVillagerData().getProfession() != villagerData.getProfession())
                info("Profession changed from %s to %s", villager.getVillagerData().getProfession(),
                        villagerData.getProfession());
        }
    }

    @EventHandler(priority = EventHandler.Priority.LOW)
    void onHighlightEvent(EntityRender.EntityHighlightEvent event) {
        if (event.getEntity() == villager) {
            event.setHasOutline(true);
            event.setColor(0xFFFF00);
        }
    }

    void fail(String format, Object... args) {
        error(format, args);
        this.disable();
    }

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
