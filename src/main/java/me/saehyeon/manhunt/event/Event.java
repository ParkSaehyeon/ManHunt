package me.saehyeon.manhunt.event;

import me.saehyeon.manhunt.*;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static me.saehyeon.manhunt.ManHunt.cantMove;
import static me.saehyeon.manhunt.ManHunt.gameSettings;
import static me.saehyeon.manhunt.Message.*;

public class Event implements Listener {
    @EventHandler
    void onWin(PlayerAdvancementDoneEvent e) {
        if(ManHunt.isTarget(e.getPlayer())) {
            if(e.getAdvancement().getKey().toString().equals("minecraft:story/follow_ender_eye")) {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if(ManHunt.isTarget(p)) {
                        p.sendTitle("§b§l승리!","엔더유적에 진입했어요.",0,50,20);
                    } else {
                        p.sendTitle("§c§l타깃을 놓침!","타깃이 엔더유적에 진입했어요.",0,50,20);
                    }
                });
                ManHunt.getInstance().Stop(RoleType.TARGET);
            }
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

            ManHunt.getInstance().Stop(RoleType.CATCHER);
        }
    }

    @EventHandler
    void onRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskLater(Main.ins, () -> {

            // 밸런스 모드 켜져있으면 허기가 10으로 고정, 재생 1 지급
            if(gameSettings.enable_balance_mode) {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100000,0,false,false,false));
                e.getPlayer().setFoodLevel(10);
            }

        },1);
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
}
