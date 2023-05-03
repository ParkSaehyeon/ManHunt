package me.saehyeon.manhunt;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Random;

public class Util {
    public static void sendActionbarAll(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message)));
    }

    public static void deleteDirectory(String path) {
        File file = new File(path);

        if(file.exists()) {
            for(File f : file.listFiles()) {

                // poi 폴더는 건들지 않기
                if(f.isDirectory() && !f.getName().equals("poi")) {
                    deleteDirectory(f.getAbsolutePath());
                } else {
                    f.delete();
                }
            }

            file.delete();
        }
    }

    public static void spreadPlayer(Location location, int radius) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            double x = location.getX()+(Math.random() * radius - radius);
            double z = location.getZ()+(Math.random() * radius - radius);
            Location loc = location.getWorld().getHighestBlockAt((int)x,(int)z).getLocation().clone().add(0,1,0);

            p.teleport(loc);
        }
    }
}
