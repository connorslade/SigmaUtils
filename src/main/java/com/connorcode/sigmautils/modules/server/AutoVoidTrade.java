package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.misc.Tick;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;

import java.util.HashSet;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class AutoVoidTrade extends Module {
    // [In Progress]
    // Settings:
    // - Trade index (The index of the slot in the villager's trade list)
    // - Delay (The delay between teleporting and trading)

    private static final NumberSetting tradeIndex =
            new NumberSetting(AutoVoidTrade.class, "Trade Index", 1, 10).value(1)
                    .description("The index of the slot in the villager's trade list. (1 is the first slot)")
                    .precision(0)
                    .build();

    private static final NumberSetting villagerDistance =
            new NumberSetting(AutoVoidTrade.class, "Villager Distance", 0, 15).value(10)
                    .description("The distance from the player that the villager must be to trade with them")
                    .precision(0)
                    .build();

    private static final NumberSetting delay = new NumberSetting(AutoVoidTrade.class, "Delay", 0, 20).value(10)
            .description("The delay in ticks between teleporting and trading")
            .precision(0)
            .build();

    VillagerEntity villager;
    boolean setOffer;
    int tradeIn;

    public AutoVoidTrade() {
        super("auto_void_trade", "Auto Void Trade", "Automatically perform void trading [EXPERIMENTAL]",
                Category.Server);
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

            info("Trading");
            client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, 2, 0,
                    SlotActionType.QUICK_MOVE, client.player);
            client.setScreen(null);
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
        info("Found villager, Interacting");
        var network = Objects.requireNonNull(client.getNetworkHandler());
        network.sendPacket(PlayerInteractEntityC2SPacket.interact(villager, false, Hand.MAIN_HAND));
    }

    void onVillagerLeaveRange() {
        if (!this.setOffer) {
            error("Villager left range before trade offer was set, disabling");
            this.disable();
            return;
        }

        // TODO: Verify that there is inventory space
        info("Villager left range, scheduling trade");
        this.tradeIn = delay.intValue();
    }

    @EventHandler
    void onPacketReceive_SetTradeOffers(PacketReceiveEvent packet) {
//        info("PACKET: %s", packet.get().getClass().getSimpleName());
//        if (packet.get() instanceof ScreenHandlerSlotUpdateS2CPacket slot)
//            info("SlotUpdate: sync: %d - rev: %d - slot: %d - stack: %s", slot.getSyncId(), slot.getRevision(), slot.getSlot(), slot.getItemStack().getItem());
        if (packet.get() instanceof ClickSlotC2SPacket slot)
            info("ClickSlot: #%d - rev: %d - slot: %d - button: %d - action: %s", slot.getSyncId(), slot.getRevision(),
                    slot.getSlot(), slot.getButton(), slot.getActionType());

        if (!enabled || !(packet.get() instanceof SetTradeOffersS2CPacket setTradeOffersS2CPacket)) return;
        info("Got trade offers packet");
        if (setTradeOffersS2CPacket.getOffers().size() < tradeIndex.value()) {
            error("Invalid trade index, disabling");
            this.disable();
            return;
        }

        // Get trade
        TradeOffer tradeOffer = setTradeOffersS2CPacket.getOffers().get(tradeIndex.intValue());
        if (tradeOffer.getUses() > tradeOffer.getMaxUses()) {
            error("Trade exhausted, disabling");
            this.disable();
            return;
        }

        // Select trade
        Objects.requireNonNull(client.getNetworkHandler())
                .sendPacket(new SelectMerchantTradeC2SPacket(tradeIndex.intValue()));
    }

    @EventHandler
    void onPacketReceive_ScreenHandlerSlotUpdateS2CPacket(PacketReceiveEvent packet) {
        if (!enabled || !(packet.get() instanceof ScreenHandlerSlotUpdateS2CPacket slotUpdate) ||
                slotUpdate.getSlot() != 2) return;

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

// /execute in minecraft:overworld run tp @s 50.46 63.00 28.41 87.00 17.55
