package com.whiteroad;

import net.jadedmc.autopickup.PickupEvent;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EventListen implements Listener {

    @EventHandler
    public void onAutoPickup(@NotNull PickupEvent event) {

        // 플레이어가 아이템을 줍는 경우에만 처리
        if (!(event.getPlayer() instanceof org.bukkit.entity.Player)) return;

        Player player = (Player) event.getPlayer();
        ItemStack item = event.getItem();

        // 특정 아이템을 확인하고 싶은 경우 여기서 확인
        // 예를 들어, 다이아몬드를 확인하는 경우:

        if (item.getType() == Material.COBBLESTONE) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "cobble_mine", amount);
        }
        if (item.getType() == Material.COAL) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "coal_mine", amount);
        }
        if (item.getType() == Material.RAW_IRON) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "iron_mine", amount);
        }
        if (item.getType() == Material.RAW_GOLD) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "gold_mine", amount);
        }
        if (item.getType() == Material.DIAMOND) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "diamond_mine", amount);
        }
        if (item.getType() == Material.EMERALD) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "emerald_mine", amount);
        }

        if (item.getType() == Material.WHEAT) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "wheat_farm", amount);
        }
        if (item.getType() == Material.BEETROOT) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "beet_farm", amount);
        }
        if (item.getType() == Material.POTATO) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "potato_farm", amount);
        }
        if (item.getType() == Material.CARROT) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "carrot_farm", amount);
        }

        if (item.getType() == Material.LEATHER) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "leather_hunt", amount);
        }
        if (item.getType() == Material.PORKCHOP || item.getType() == Material.BEEF || item.getType() == Material.CHICKEN || item.getType() == Material.MUTTON || item.getType() == Material.RABBIT) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "meat_hunt", amount);
        }

        if (item.getType() == Material.SALMON) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "salmon_fish", amount);
        }
        if (item.getType() == Material.COD) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "cod_fish", amount);
        }
        if (item.getType() == Material.PUFFERFISH) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "puffer_fish", amount);
        }
        // 쓰레기 & 보물 관련 감지 만들어야함!
    }

    @EventHandler
    public void onMobKill(@NotNull EntityDeathEvent event) {
        // 죽은 엔티티가 적대적 몬스터인지 확인
        if (event.getEntity() instanceof Monster) {
            // 마지막으로 피해를 준 엔티티가 플레이어인지 확인
            if (event.getEntity().getKiller() instanceof Player) {
                Player player = event.getEntity().getKiller();
                CP.QuestCheckKill(player, "enemy_hunt");
            }
        }

        if (event.getEntity() instanceof Cow) {
            // 마지막으로 피해를 준 엔티티가 플레이어인지 확인
            if (event.getEntity().getKiller() instanceof Player) {
                Player player = event.getEntity().getKiller();
                CP.QuestCheckKill(player, "cow_hunt");
            }
        }

        if (event.getEntity() instanceof Pig) {
            // 마지막으로 피해를 준 엔티티가 플레이어인지 확인
            if (event.getEntity().getKiller() instanceof Player) {
                Player player = event.getEntity().getKiller();
                CP.QuestCheckKill(player, "pig_hunt");
            }
        }

        if (event.getEntity() instanceof Rabbit) {
            // 마지막으로 피해를 준 엔티티가 플레이어인지 확인
            if (event.getEntity().getKiller() instanceof Player) {
                Player player = event.getEntity().getKiller();
                CP.QuestCheckKill(player, "rabbit_hunt");
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        // 플레이어가 아이템을 줍는 경우에만 처리
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        ItemStack item = event.getItem().getItemStack();

        // 특정 아이템을 확인하고 싶은 경우 여기서 확인
        // 예를 들어, 다이아몬드를 확인하는 경우:

        if (item.getType() == Material.COBBLESTONE) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "cobble_mine", amount);
        }
        if (item.getType() == Material.COAL) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "coal_mine", amount);
        }
        if (item.getType() == Material.RAW_IRON) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "iron_mine", amount);
        }
        if (item.getType() == Material.RAW_GOLD) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "gold_mine", amount);
        }
        if (item.getType() == Material.DIAMOND) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "diamond_mine", amount);
        }
        if (item.getType() == Material.EMERALD) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "emerald_mine", amount);
        }

        if (item.getType() == Material.WHEAT) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "wheat_farm", amount);
        }
        if (item.getType() == Material.BEETROOT) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "beet_farm", amount);
        }
        if (item.getType() == Material.POTATO) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "potato_farm", amount);
        }
        if (item.getType() == Material.CARROT) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "carrot_farm", amount);
        }

        if (item.getType() == Material.LEATHER) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "leather_hunt", amount);
        }
        if (item.getType() == Material.PORKCHOP || item.getType() == Material.BEEF || item.getType() == Material.CHICKEN || item.getType() == Material.MUTTON || item.getType() == Material.RABBIT) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "meat_hunt", amount);
        }

        if (item.getType() == Material.SALMON) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "salmon_fish", amount);
        }
        if (item.getType() == Material.COD) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "cod_fish", amount);
        }
        if (item.getType() == Material.PUFFERFISH) {
            int amount = item.getAmount(); // 줍는 아이템의 개수
            CP.QuestCheckItem(player, "puffer_fish", amount);
        }
        // 쓰레기 & 보물 관련 감지 만들어야함!
    }
}
