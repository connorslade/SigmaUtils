package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.DynamicSelectorSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.config.settings.list.SimpleSelector;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.Priority;
import com.connorcode.sigmautils.event.misc.GameLifecycle;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.event.render.EntityRender;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.DocumentedEnum;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Automatically cycles a villager's trades until you get the desired trade. " +
                          "To use put a few workstations in your offhand and the tool to break it in your main hand. Enable the module and place the workstation.",
        documentation = "See it in action [here](https://cdn.discordapp.com/attachments/837875259092500493/1128781591708180510/java_MFuVkeZ5mi.mp4)")
public class AutoTradeCycle extends Module {
    NumberSetting villagerDistance =
            new NumberSetting(AutoTradeCycle.class, "Villager Distance", 0, 15).value(10)
                    .description("The distance from the player that the villager must be to be selected")
                    .precision(0)
                    .build();
    EnumSetting<TradeType> tradeType =
            new EnumSetting<>(AutoTradeCycle.class, "Trade Type", TradeType.class)
                    .description("The type of trade to look for.")
                    .value(TradeType.EnchantedBook)
                    .build();
    BoolSetting verbose = new BoolSetting(AutoTradeCycle.class, "Verbose")
            .description("Print extra information to chat.")
            .build();
    BoolSetting alertSound = new BoolSetting(AutoTradeCycle.class, "Alert Sound")
            .description("Play a sound when the trade is found.")
            .build();
    DynamicSelectorSetting<Enchantment> enchantment =
            new DynamicSelectorSetting<>(AutoTradeCycle.class, "Enchantment", EnchantmentList::new)
                    .description("The enchantments that will stop the cycling.")
                    .category("Enchantment")
                    .build();
    NumberSetting enchantmentLevel =
            new NumberSetting(AutoTradeCycle.class, "Enchantment Level", 1, 1)
                    .value(1)
                    .precision(0)
                    .category("Enchantment")
                    .description("The min level of the enchantment required.")
                    .build();
    DynamicSelectorSetting<Item> item = new DynamicSelectorSetting<>(AutoTradeCycle.class, "Item", ItemList::new)
            .description("The items that will stop the cycling.")
            .category("Item")
            .build();
    NumberSetting itemAmount = new NumberSetting(AutoTradeCycle.class, "Item Amount", 1, 16)
            .value(1)
            .precision(0)
            .category("Item")
            .description("The min amount of the item required.")
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
        if (loadedVillagers.isEmpty()) fail("No villagers in range, disabling");
        else this.villager = loadedVillagers.stream().min((v1, v2) -> {
            var d1 = v1.getPos().distanceTo(client.player.getPos());
            var d2 = v2.getPos().distanceTo(client.player.getPos());
            return Double.compare(d1, d2);
        }).orElseThrow();
    }

    @EventHandler
    void onWorldClose(GameLifecycle.WorldCloseEvent event) {
        villager = null;
    }

    @EventHandler
    void onPacketReceive_EntityTrackerUpdateS2CPacket(PacketReceiveEvent event) {
        if (!enabled || !(event.get() instanceof EntityTrackerUpdateS2CPacket trackerUpdate) || villager == null ||
                trackerUpdate.id() != villager.getId()) return;

        var trackedValues = trackerUpdate.trackedValues();
        if (trackedValues.isEmpty()) return;
        for (var trackedValue : trackedValues) {
            if (trackedValue.id() != 18 || !(trackedValue.value() instanceof VillagerData villagerData)) continue;
            if (villager.getVillagerData().getProfession() != villagerData.getProfession()) {
                var network = Objects.requireNonNull(client.getNetworkHandler());
                if (villagerData.getProfession() == VillagerProfession.NONE) {
                    if (!(client.crosshairTarget instanceof BlockHitResult blockHitResult)) {
                        fail("No block targeted, disabling.");
                        return;
                    }
                    network.sendPacket(new PlayerInteractBlockC2SPacket(Hand.OFF_HAND, blockHitResult, 0));
                } else network.sendPacket(PlayerInteractEntityC2SPacket.interact(villager, false, Hand.MAIN_HAND));
            }
        }
    }

    @EventHandler
    void onPacketReceive_SetTradeOffers(PacketReceiveEvent packet) {
        if (!enabled || client.player == null ||
                !(packet.get() instanceof SetTradeOffersS2CPacket setTradeOffersS2CPacket)) return;

        for (var i : setTradeOffersS2CPacket.getOffers()) {
            switch (this.tradeType.value()) {
                case Item -> {
                    if (i.getSellItem().getItem() != this.item.value() ||
                            i.getSellItem().getCount() < this.itemAmount.intValue()) continue;
                    info("FOUND ITEM");
                    onFind();
                    return;
                }
                case EnchantedBook -> {
                    if (!(i.getSellItem().getItem() instanceof EnchantedBookItem data)) continue;
                    var settingEnchantment = Registries.ENCHANTMENT.getId(this.enchantment.value());
                    for (int j = 0; j < data.size(); j++) {
                        var enchantment = new Identifier(data.getCompound(j).getString("id"));
                        var enchantmentLev = data.getCompound(j).getInt("lvl");
                        if (verbose.value()) info("%s %s", enchantment, enchantmentLev);
                        if (!enchantment.equals(settingEnchantment) || enchantmentLev < enchantmentLevel.intValue())
                            continue;
                        info("FOUND ENCHANTMENT");
                        onFind();
                        return;
                    }
                }
            }
        }

        if (!(client.crosshairTarget instanceof BlockHitResult blockHitResult)) fail("No block targeted, disabling.");
        else breakBlock(blockHitResult.getBlockPos());
        packet.cancel();
    }

    @EventHandler(priority = Priority.LOW)
    void onHighlightEvent(EntityRender.EntityHighlightEvent event) {
        if (!enabled || event.getEntity() != villager) return;
        event.setHasOutline(true);
        event.setColor(0xFFFF00);
    }

    void onFind() {
        this.disable();
        if (this.alertSound.value())
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_BELL_RESONATE, 1f));
    }

    void breakBlock(BlockPos block) {
        var network = Objects.requireNonNull(client.getNetworkHandler());
        network.sendPacket(
                new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, block, Direction.UP));
        network.sendPacket(
                new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, block, Direction.UP));
    }

    void fail(String format, Object... args) {
        error(format, args);
        this.disable();
    }

    enum TradeType {
        @DocumentedEnum("Looks for a specific enchantment on an enchanted book.")
        EnchantedBook,
        @DocumentedEnum("Looks for a specific item type.")
        Item
    }

    static class ItemList extends SimpleSelector<Item> {

        public ItemList(DynamicSelectorSetting<Item> setting) {
            super(setting, Registries.ITEM);
        }

        @Override
        public String getDisplay(Item resource) {
            if (resource == null) return "None";
            return resource.getName().getString();
        }

        @Override
        public String type() {
            return "Item";
        }
    }

    class EnchantmentList extends SimpleSelector<Enchantment> {

        public EnchantmentList(DynamicSelectorSetting<Enchantment> setting) {
            super(setting, Registries.ENCHANTMENT);
        }

        @Override
        protected boolean filter(Enchantment resource) {
            return !(resource instanceof SwiftSneakEnchantment) && !(resource instanceof SoulSpeedEnchantment);
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

        @Override
        public String type() {
            return "Enchantment";
        }
    }
}
