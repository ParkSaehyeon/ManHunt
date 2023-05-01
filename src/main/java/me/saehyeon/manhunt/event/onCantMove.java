package me.saehyeon.manhunt.event;

import me.saehyeon.manhunt.ManHunt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import static me.saehyeon.manhunt.ManHunt.cantMove;

/**
 * 움직이지 않을 때(cantMove = true일때)의 이벤트를 처리합니다.
 */
public class onCantMove implements Listener {
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
    void onMove(PlayerMoveEvent e) {
        if(!ManHunt.isTarget(e.getPlayer())) {
            e.setCancelled(ManHunt.cantMove);
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
