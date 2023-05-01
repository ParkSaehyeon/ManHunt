package me.saehyeon.manhunt;

import me.saehyeon.manhunt.event.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Main extends JavaPlugin implements CommandExecutor, TabCompleter {
    public static Main ins;
    @Override
    public void onEnable() {
        ins = this;
        Bukkit.getPluginCommand("멘헌트").setExecutor(new onCommand());
        Bukkit.getPluginCommand("멘헌트").setTabCompleter(new onTabComplete());
        Bukkit.getPluginManager().registerEvents(new Event(),this);
        Bukkit.getPluginManager().registerEvents(new onCantMove(),this);
        Bukkit.getPluginManager().registerEvents(new GUIEvent(),this);

        ManHunt.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        getDataFolder().mkdir();
        GameSettings.load();
    }

    @Override
    public void onDisable() {
        GameSettings.save();
    }

    public static void DebugLog(String message) {
        if(ManHunt.debugMode) ins.getLogger().log(Level.INFO, message);
    }
}
