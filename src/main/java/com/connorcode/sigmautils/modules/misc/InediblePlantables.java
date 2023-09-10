package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event._interface.Interact;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;

import java.util.Arrays;

@ModuleInfo(
        description = "Right-clicking non-farmland blocks won't start eating plantable crops. Useful for manual farming. You can still eat the plantables by eating them while not looking at a block.",
        documentation = "You can Current Plantables: Potatoes, Carrots and Sweet Berries"
)
public class InediblePlantables extends Module {
    static Plantable[] plantables = new Plantable[]{
            new Plantable(Items.POTATO, Items.FARMLAND),
            new Plantable(Items.CARROT, Items.FARMLAND),
            new Plantable(Items.SWEET_BERRIES, new Item[]{
                    Items.GRASS_BLOCK,
                    Items.DIRT,
                    Items.PODZOL,
                    Items.COARSE_DIRT,
                    Items.FARMLAND,
                    Items.MOSS_BLOCK
            })
    };

    @EventHandler
    void onUseItem(Interact.InteractBlockEvent event) {
        if (!enabled) return;
        var pos = event.getHitResult().getBlockPos();
        var block = event.getPlayer().getEntityWorld().getBlockState(pos).getBlock().asItem();

        var holding = event.getPlayer().getStackInHand(event.getHand());
        var plantableRaw = Arrays.stream(plantables).filter(item -> item.item.equals(holding.getItem())).findFirst();
        if (plantableRaw.isEmpty()) return;

        // Return if item can be planted on block
        var plantable = plantableRaw.get();
        if (plantable.blocksContains(block)) return;
        event.setReturnValue(ActionResult.CONSUME);
    }

    static class Plantable {
        Item item;
        Item[] blocks;

        Plantable(Item item, Item[] blocks) {
            this.item = item;
            this.blocks = blocks;
        }

        Plantable(Item item, Item block) {
            this.item = item;
            this.blocks = new Item[]{block};
        }

        boolean blocksContains(Item item) {
            return Arrays.asList(blocks).contains(item);
        }
    }
}
