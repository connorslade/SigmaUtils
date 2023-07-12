package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.misc.GameLifecycle;
import com.connorcode.sigmautils.event.misc.Tick;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.misc.util.InventoryUtils;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.village.TradeOffer;

import java.util.HashSet;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Automatically performs void trading.")
public class AutoVoidTrade extends Module {
    private final NumberSetting tradeIndex =
            new NumberSetting(AutoVoidTrade.class, "Trade Index", 1, 10).value(1)
                    .description("The index of the slot in the villager's trade list. (1 is the first slot)")
                    .precision(0)
                    .build();

    private final NumberSetting villagerDistance =
            new NumberSetting(AutoVoidTrade.class, "Villager Distance", 0, 15).value(10)
                    .description("The distance from the player that the villager must be to trade with them")
                    .precision(0)
                    .build();

    private final NumberSetting delay = new NumberSetting(AutoVoidTrade.class, "Delay", 0, 20).value(15)
            .description("The delay in ticks between teleporting and trading")
            .precision(0)
            .build();

    private final BoolSetting autoRestart = new BoolSetting(AutoVoidTrade.class, "Auto Restart")
            .description("Tries to automatically activate the void-trading water stream. " +
                    "This is just done by interacting with the block the player is looking at after trading.")
            .build();

    VillagerEntity villager;
    boolean setOffer;
    int tradeIn = -1;

    @EventHandler
    private void onWorldClose(GameLifecycle.WorldCloseEvent event) {
        villager = null;
        setOffer = false;
        tradeIn = -1;
    }

    @Override
    public void disable() {
        super.disable();
        this.onWorldClose(null);
    }

    @EventHandler
    public void onTick(Tick.GameTickEvent event) {
        if (!enabled || client.player == null || client.world == null || client.interactionManager == null) return;

        if (tradeIn >= 0 && --tradeIn == 0) {
            if (!(client.currentScreen instanceof MerchantScreen)) {
                error("Trade screen closed before villager left range, disabling");
                this.disable();
                return;
            }

            client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, 2, 0,
                    SlotActionType.QUICK_MOVE, client.player);
            client.setScreen(null);

            if (autoRestart.value()) {
                if (client.crosshairTarget == null || client.crosshairTarget.getType() != HitResult.Type.BLOCK) {
                    error("Player is not looking at a block, disabling");
                    this.disable();
                    return;
                }

                client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND,
                        (BlockHitResult) client.crosshairTarget);
            }
        }

        var loadedVillagers = new HashSet<>(client.world.getEntitiesByClass(VillagerEntity.class,
                client.player.getBoundingBox().expand(villagerDistance.intValue()), villagerEntity -> true));
        if (loadedVillagers.size() > 1) {
            error("Multiple villagers in range, disabling");
            this.disable();
            return;
        }

        if (loadedVillagers.isEmpty() && villager != null) {
            onVillagerLeaveRange();
            villager = null;
        }

        if (!loadedVillagers.isEmpty() && villager == null) {
            villager = loadedVillagers.stream().iterator().next();
            onVillagerEnterRange();
        }
    }

    void onVillagerEnterRange() {
        var network = Objects.requireNonNull(client.getNetworkHandler());
        network.sendPacket(PlayerInteractEntityC2SPacket.interact(villager, false, Hand.MAIN_HAND));
    }

    void onVillagerLeaveRange() {
        if (!this.setOffer) {
            error("Villager left range before trade offer was set, disabling");
            this.disable();
            return;
        }

        this.tradeIn = delay.intValue();
        this.setOffer = false;
    }

    @EventHandler
    void onPacketReceive_SetTradeOffers(PacketReceiveEvent packet) {
        if (!enabled || client.player == null ||
                !(packet.get() instanceof SetTradeOffersS2CPacket setTradeOffersS2CPacket)) return;
        if (setTradeOffersS2CPacket.getOffers().size() < tradeIndex.intValue() - 1) {
            error("Invalid trade index, disabling");
            this.disable();
            return;
        }

        // Get trade
        TradeOffer tradeOffer = setTradeOffersS2CPacket.getOffers().get(tradeIndex.intValue() - 1);
        if (tradeOffer.getUses() > tradeOffer.getMaxUses()) {
            error("Trade exhausted, disabling");
            this.disable();
            return;
        }

        // Verify player has the required items
        var requiredItems = new ItemStack[]{
                tradeOffer.getAdjustedFirstBuyItem(),
                tradeOffer.getSecondBuyItem()
        };
        for (var item : requiredItems) {
            if (item.isEmpty()) continue;
            var itemCount = client.player.getInventory().count(item.getItem());
            if (itemCount < item.getCount()) {
                error("Missing required item (%s), disabling", item.getItem().getName().getString());
                this.disable();
                return;
            }
        }

        // Verify there is enough inventory space
        var resultItem = tradeOffer.getSellItem();
        if (!resultItem.isEmpty() && !InventoryUtils.canFitStack(client.player.getInventory(), resultItem)) {
            error("Not enough inventory space, disabling");
            this.disable();
            return;
        }

        // Select trade
        Objects.requireNonNull(client.getNetworkHandler())
                .sendPacket(new SelectMerchantTradeC2SPacket(tradeIndex.intValue() - 1));
    }

    @EventHandler
    void onPacketReceive_ScreenHandlerSlotUpdateS2CPacket(PacketReceiveEvent packet) {
        if (!enabled || !(packet.get() instanceof ScreenHandlerSlotUpdateS2CPacket slotUpdate) ||
                slotUpdate.getSlot() != 2 || slotUpdate.getSyncId() < 0) return;

        // Verify this packet is for the merchant screen
        if (!(client.currentScreen instanceof MerchantScreen)) {
            error("Not in merchant screen, disabling");
            this.disable();
            return;
        }

        // Signal that the trade offer has been set
        this.setOffer = true;
    }
}
