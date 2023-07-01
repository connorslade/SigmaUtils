package com.connorcode.sigmautils.misc.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
    public static boolean canFitStack(Inventory inv, ItemStack stack) {
        for (int i = 0; i < inv.size(); i++)
            if (inv.getStack(i).isEmpty())
                return true;

        for (int i = 0; i < inv.size(); i++)
            if (inv.getStack(i).getItem() == stack.getItem() &&
                    inv.getStack(i).getCount() + stack.getCount() <= stack.getMaxCount())
                return true;

        return false;
    }
}
