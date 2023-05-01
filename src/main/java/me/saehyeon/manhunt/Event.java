package me.saehyeon.manhunt;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;

import static me.saehyeon.manhunt.ManHunt.cantMove;
import static me.saehyeon.manhunt.ManHunt.gameSettings;
import static me.saehyeon.manhunt.Message.*;

public class Event implements Listener {
    @EventHandler
    void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = ((Player) e.getEntity());

            // 타깃을 제외한 인원은 움직이지 못할 때는 데미지를 입지 않음
            if(cantMove && !ManHunt.isTarget(p)) {
                e.setCancelled(true);
            }
        }
    }
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
                    case "§f§l거리 보이기" -> {
                        gameSettings.enable_show_distance = !gameSettings.enable_show_distance;
                        enabled = gameSettings.enable_show_distance;
                    }

                    case "§f§l집결" -> {
                        gameSettings.enable_unite = !gameSettings.enable_unite;
                        enabled = gameSettings.enable_unite;
                    }

                    case "§f§l포만감" -> {
                        gameSettings.enable_anti_hunger = !gameSettings.enable_anti_hunger;
                        enabled = gameSettings.enable_anti_hunger;
                    }

                    case "§f§l밸런스" -> {
                        gameSettings.enable_balance_mode = !gameSettings.enable_balance_mode;
                        enabled = gameSettings.enable_balance_mode;
                        if (gameSettings.enable_anti_hunger) {
                            gameSettings.enable_anti_hunger = false;
                            Bukkit.broadcastMessage("밸런스 모드가 활성화되었으므로, 포만감 모드가 §c비활성화되었습니다.");
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

                Bukkit.broadcastMessage("§7"+p.getName()+"§f에 의해 타깃의 기본 아이템이 "+ManHunt.targetKitType.krName+"§f(으)로 설정되었습니다.");
                ManHunt.getInstance().openTargetItemGUI(p);
            }
        }
    }

    @EventHandler
    void onCloseInventory(InventoryCloseEvent e) {
        ManHunt.tasks.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    void onMove(PlayerMoveEvent e) {
        if(!ManHunt.isTarget(e.getPlayer())) {
            e.setCancelled(ManHunt.cantMove);
        }
    }

    @EventHandler
    void onDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        Player target = Bukkit.getPlayer(ManHunt.targetUUID);

        // 타깃이 죽음 -> 술래 승리, 게임종료
        if(target != null && target == p) {
            if(gameSettings.lolicon_mode) {
                Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§f§l게임종료!","§f타깃이 강간당했어요."));
            } else {
                Bukkit.getOnlinePlayers().forEach(_p -> _p.sendTitle("§f§l게임종료!","§f타깃이 죽었어요."));
            }

            ManHunt.getInstance().Stop();
        }
    }

    @EventHandler
    void onWorldChange(PlayerPortalEvent e) {
        Player p = e.getPlayer();

        Player target = Bukkit.getPlayer(ManHunt.targetUUID);

        if(target != null) {
            if(target == p) {

                // 이 월드에서의 마지막 위치 저장하기
                ManHunt.targetLastLoc.put(p.getWorld(), p.getLocation().clone());

            }
        }
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        e.getPlayer().setScoreboard(ManHunt.scoreboard);

        if(ManHunt.isTarget(e.getPlayer()) && !ManHunt.isStartCountDowning && ManHunt.isGaming) {
            ManHunt.getInstance().StopWaitTarget();
        }
    }

    @EventHandler
    void onQuit(PlayerQuitEvent e) {

        if(ManHunt.isTarget(e.getPlayer()) && !ManHunt.isStartCountDowning && ManHunt.isGaming) {
            ManHunt.getInstance().StartWaitTarget();
        }
    }

    @EventHandler
    void onFoodLevel(FoodLevelChangeEvent e) {
        if(gameSettings.enable_anti_hunger || gameSettings.enable_balance_mode) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    void onInteraction(PlayerInteractEvent e) {
        // 타깃을 제외한 인원은 움직이지 못할 때는 데미지를 입지 않음
        if(cantMove && !ManHunt.isTarget(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    void onAtEntityInteraction(PlayerInteractAtEntityEvent e) {
        // 타깃을 제외한 인원은 움직이지 못할 때는 데미지를 입지 않음
        if(cantMove && !ManHunt.isTarget(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    void onEntityInteraction(PlayerInteractEntityEvent e) {
        // 타깃을 제외한 인원은 움직이지 못할 때는 데미지를 입지 않음
        if(cantMove && !ManHunt.isTarget(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    void onDrop(PlayerDropItemEvent e) {
        // 타깃을 제외한 인원은 움직이지 못할 때는 데미지를 입지 않음
        if(cantMove && !ManHunt.isTarget(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    void onPickup(PlayerPickupItemEvent e) {
        // 타깃을 제외한 인원은 움직이지 못할 때는 데미지를 입지 않음
        if(cantMove && !ManHunt.isTarget(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
