package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.event.network.PacketReceiveCallback;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
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
    private static final HashSet<Integer> traided = new HashSet<>();
    private static int waiting = 0;

    public AutoVoidTrade() {
        super("auto_void_trade", "Auto Void Trade", "Automatically perform void trading [EXPERIMENTAL]",
                Category.Server);
    }

    @Override
    public void tick() {
        super.tick();

        if (!enabled || waiting < 0 || client.player == null || client.world == null) return;
        if (waiting != 0) {
            waiting--;
            return;
        }

        HashSet<VillagerEntity> loadedVillagers = new HashSet<>(client.world.getEntitiesByClass(VillagerEntity.class,
                client.player.getBoundingBox().expand(villagerDistance.intValue()), villagerEntity -> true));
        traided.removeIf(id -> !loadedVillagers.contains((VillagerEntity) client.world.getEntityById(id)));
        loadedVillagers.removeIf(villagerEntity -> traided.contains(villagerEntity.getId()));
        if (loadedVillagers.isEmpty()) return;

        VillagerEntity next = loadedVillagers.stream().iterator().next();
        Objects.requireNonNull(client.getNetworkHandler())
                .sendPacket(PlayerInteractEntityC2SPacket.interact(next, false, Hand.MAIN_HAND));
        waiting = -1;
    }

    @Override
    public void disable(MinecraftClient client) {
        super.disable(client);
        waiting = 0;
        traided.clear();
    }

    @Override
    public void init() {
        super.init();

        PacketReceiveCallback.EVENT.register(packet -> {
            if (!enabled) return;
            if (packet.get() instanceof SetTradeOffersS2CPacket setTradeOffersS2CPacket) {
                if (setTradeOffersS2CPacket.getOffers().size() <= tradeIndex.value()) error("Invalid trade index");
                TradeOffer tradeOffer = setTradeOffersS2CPacket.getOffers().get(tradeIndex.intValue());

                if (tradeOffer.getUses() > tradeOffer.getMaxUses()) {
                    error("Trade exhausted");
                    return;
                }
                Objects.requireNonNull(client.getNetworkHandler())
                        .sendPacket(new SelectMerchantTradeC2SPacket(tradeIndex.intValue()));

                if (client.currentScreen instanceof MerchantScreen merchantScreen) {
                    System.out.println("Merchant screen open");
                    Slot slot = merchantScreen.getScreenHandler().getSlot(2);
//                    if (slot.hasStack()) {
                    System.out.println("Slot has stack");
                    Objects.requireNonNull(client.interactionManager)
                            .clickSlot(merchantScreen.getScreenHandler().syncId, slot.id, 0,
                                    SlotActionType.QUICK_MOVE, client.player);
                    waiting = delay.intValue();
//                    }
                }
            }
        });
    }

    private void error(String message) {
        Objects.requireNonNull(client.player)
                .sendMessage(Text.of("[SIGMAUTILS::AutoVoidTrade] " + message), false);
        waiting = delay.intValue();
    }
}

// /execute in minecraft:overworld run tp @s 50.46 63.00 28.41 87.00 17.55