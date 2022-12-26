package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class InventoryMove extends Module {
    public InventoryMove() {
        super("inventory_move", "Inventory Move",
                "Lets you move your player with WASD even when in a screen (excluding chat)", Category.Interface);
    }
}
