package me.saehyeon.manhunt.event;

import me.saehyeon.manhunt.ManHunt;
import me.saehyeon.manhunt.TargetKitType;
import me.saehyeon.manhunt.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static me.saehyeon.manhunt.ManHunt.gameSettings;
import static me.saehyeon.manhunt.Message.*;

/**
 * GUI 관련 이벤트를 관리합니다.
 */
public class GUIEvent implements Listener {
    @EventHandler
    void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player)e.getWhoClicked();

        if(e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) {

            TaskType task = ManHunt.tasks.get(p.getUniqueId());
            String itemName = e.getCurrentItem().getItemMeta().getDisplayName();

            // ---- TaskType: 타깃 설정 GUI에서의 클릭
            if(task == TaskType.TARGET_SETTING) {
                e.setCancelled(true);

                if(e.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                    String targetName = ChatColor.stripColor(itemName);
                    Player target = Bukkit.getPlayer(targetName);

                    if(target != null) {
                        Bukkit.broadcastMessage("타깃이 §7"+p.getName()+"§f에 의해 §7"+target.getName()+"§f으로 설정되었습니다.");
                        ManHunt.targetUUID = target.getUniqueId();
                    } else {
                        p.closeInventory();
                        p.sendMessage("§c타깃을 설정하려고 했으나, "+targetName+"(이)라는 이름의 플레이어는 타깃으로 설정할 수 없습니다.");
                        p.sendMessage("§c해당 플레이어를 서버에서 찾을 수 없는 것 같습니다.");
                    }
                }

                // 플레이어 자동 설정으로 전환
                else if(e.getCurrentItem().getType() == Material.BARRIER) {
                    if(itemName.equals(GUI_TS_NONE_SELECT)) {
                        ManHunt.targetUUID = null;
                        Bukkit.broadcastMessage("§7"+p.getName()+"§f에 의해 게임 시작 시 타깃이 랜덤으로 결정됩니다.");
                    }
                }
            }

            // ---- TaskType: 게임 설정
            else if(task == TaskType.GAME_SETTING) {
                e.setCancelled(true);

                boolean enabled;

                switch (itemName) {
                    case GUI_GS_DISTANCE -> {
                        gameSettings.enable_show_distance = !gameSettings.enable_show_distance;
                        enabled = gameSettings.enable_show_distance;
                    }

                    case GUI_GS_UNITE -> {
                        gameSettings.enable_unite = !gameSettings.enable_unite;
                        enabled = gameSettings.enable_unite;
                    }

                    case GUI_GS_ANTI_HUNGER -> {
                        gameSettings.enable_anti_hunger = !gameSettings.enable_anti_hunger;
                        enabled = gameSettings.enable_anti_hunger;
                    }

                    case GUI_GS_BALANCE -> {
                        gameSettings.enable_balance_mode = !gameSettings.enable_balance_mode;
                        enabled = gameSettings.enable_balance_mode;
                        if (gameSettings.enable_anti_hunger) {
                            gameSettings.enable_anti_hunger = false;
                            Bukkit.broadcastMessage(GUI_GS_BALANCE_DISABLED_BY_HUNGER);
                        }
                    }

                    default -> {
                        return;
                    }
                }

                ManHunt.getInstance().openGameSettingGUI(p);
                Bukkit.broadcastMessage("§7"+p.getName()+"§f(이)가 "+itemName+"§f(을)를"+(enabled ? MODE_ENABLED : MODE_DISABLED));
            }

            // ---- TaskType: 타깃 기본템 설정 GUI
            else if(task == TaskType.TARGET_DEFALT_ITEM_SETTING) {
                e.setCancelled(true);

                //TODO 이거 해야됫듯
                switch (itemName) {
                    case GUI_TI_NONE           -> ManHunt.targetKitType = TargetKitType.NONE;
                    case GUI_TI_WOOD_SET       -> ManHunt.targetKitType = TargetKitType.WOOD;
                    case GUI_TI_STONE_SET      -> ManHunt.targetKitType = TargetKitType.STONE;
                    case GUI_TI_IRON_SET       -> ManHunt.targetKitType = TargetKitType.IRON;
                    case GUI_TI_DIAMOND_SET    -> ManHunt.targetKitType = TargetKitType.DIAMOND;
                    case GUI_TI_NETHERITE_SET  -> ManHunt.targetKitType = TargetKitType.NETHERITE;
                }

                Bukkit.broadcastMessage("§7"+p.getName()+"§f에 의해 타깃의 기본 아이템이 "+ManHunt.targetKitType.getKRName()+"§f(으)로 설정되었습니다.");
                ManHunt.getInstance().openTargetItemGUI(p);
            }
        }
    }

    @EventHandler
    void onCloseInventory(InventoryCloseEvent e) {
        ManHunt.tasks.remove(e.getPlayer().getUniqueId());
    }
}
