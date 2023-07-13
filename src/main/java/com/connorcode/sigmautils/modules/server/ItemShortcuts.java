package com.connorcode.sigmautils.modules.server;

import com.connorcode.sigmautils.config.settings.KeyBindSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.misc.Tick;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Lets you switch to your weapon or wool with a keybind. Future plans to let you select different item types.",
        documentation = "Useful for binging to a mouse button for quick access to your weapon or wool.")
public class ItemShortcuts extends Module {
    final Item[] swords = new Item[]{
            Items.NETHERITE_SWORD,
            Items.DIAMOND_SWORD,
            Items.IRON_SWORD,
            Items.GOLDEN_SWORD,
            Items.STONE_SWORD,
            Items.WOODEN_SWORD
    };
    final Item[] wool = new Item[]{
            Items.WHITE_WOOL,
            Items.BLACK_WOOL,
            Items.BLUE_WOOL,
            Items.BROWN_WOOL,
            Items.CYAN_WOOL,
            Items.GRAY_WOOL,
            Items.GREEN_WOOL,
            Items.LIGHT_BLUE_WOOL,
            Items.LIGHT_GRAY_WOOL,
            Items.LIME_WOOL,
            Items.MAGENTA_WOOL,
            Items.ORANGE_WOOL,
            Items.PINK_WOOL,
            Items.PURPLE_WOOL,
            Items.RED_WOOL,
            Items.YELLOW_WOOL
    };
    KeyBindSetting weaponShortcut =
            new KeyBindSetting(ItemShortcuts.class, "Weapon Shortcut").description("Shortcut to switch to your weapon")
                    .showTitle(true)
                    .category("Shortcuts")
                    .build();
    KeyBindSetting woolShortcut =
            new KeyBindSetting(ItemShortcuts.class, "Wool Shortcut").description("Shortcut to switch to your wool")
                    .showTitle(true)
                    .category("Shortcuts")
                    .build();

    @EventHandler
    public void onTick(Tick.GameTickEvent event) {
        if (client.currentScreen != null) return;
        if (woolShortcut.pressed()) switchTo(wool);
        if (weaponShortcut.pressed()) switchTo(swords);
    }

    private void switchTo(Item[] items) {
        if (client.player == null) return;
        var inventory = client.player.getInventory();
        findItem(i -> Arrays.stream(items).anyMatch(s -> s.equals(i.getItem()))).ifPresent(
                integer -> inventory.selectedSlot = integer);
    }

    Optional<Integer> findItem(ItemFinder itemFinder) {
        var inventory = Objects.requireNonNull(client.player).getInventory();
        for (int i = 0; i < 9; i++)
            if (itemFinder.test(inventory.main.get(i))) return Optional.of(i);
        return Optional.empty();
    }

    interface ItemFinder {
        boolean test(ItemStack itemStack);
    }
}
