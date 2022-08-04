package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class PortalInventory extends BasicModule {
    public PortalInventory() {
        super("portal_inventory", "Portal Inventory", "Allows you to open your inventory when you are in a portal",
                Category.Interface);
    }
}
