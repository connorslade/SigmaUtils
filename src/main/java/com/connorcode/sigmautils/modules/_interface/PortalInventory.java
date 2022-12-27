package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class PortalInventory extends Module {
    public PortalInventory() {
        super("portal_inventory", "Portal Inventory", "Allows you to open your inventory when you are in a portal",
                Category.Interface);
    }
}
