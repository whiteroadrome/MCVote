package com.whiteroad;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class util {
    public static boolean removeTicketNBT(Player player, Material material, Integer Value) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    if (meta.getCustomModelData() == Value) {
                        item.setAmount(item.getAmount() - 1);
                        if (item.getAmount() == 0) {
                            player.getInventory().remove(item);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
