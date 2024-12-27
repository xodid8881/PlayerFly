package org.hwabeag.playerfly;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.hwabeag.playerfly.commands.ItemCommand;
import org.hwabeag.playerfly.commands.MainCommand;
import org.hwabeag.playerfly.config.ConfigManager;
import org.hwabeag.playerfly.events.BookClickEvent;
import org.hwabeag.playerfly.events.InvClickEvent;
import org.hwabeag.playerfly.events.JoinEvent;
import org.hwabeag.playerfly.expansions.PlayerFlyExpansion;
import org.hwabeag.playerfly.schedules.FlyCheckTask;
import org.hwabeag.playerfly.schedules.PlayerFlyTask;

    public final class PlayerFly extends JavaPlugin {
        private static ConfigManager configManager;
        private FileConfiguration config;

        public PlayerFly() {
        }

        public static org.hwabeag.playerfly.PlayerFly getPlugin() {
            return (org.hwabeag.playerfly.PlayerFly)JavaPlugin.getPlugin(org.hwabeag.playerfly.PlayerFly.class);
        }

        public static void getConfigManager() {
            if (configManager == null) {
                configManager = new ConfigManager();
            }

        }

        private void registerEvents() {
            this.getServer().getPluginManager().registerEvents(new BookClickEvent(), this);
            this.getServer().getPluginManager().registerEvents(new JoinEvent(), this);
            this.getServer().getPluginManager().registerEvents(new InvClickEvent(), this);
        }

        private void registerCommands() {
            Objects.requireNonNull(getServer().getPluginCommand("플라이관리")).setExecutor(new MainCommand());
            Objects.requireNonNull(getServer().getPluginCommand("플라이권")).setExecutor(new ItemCommand());
        }

        public void onEnable() {
            Bukkit.getLogger().info("[PlayerTime] Enable");
            getConfigManager();
            this.registerCommands();
            this.registerEvents();
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new PlayerFlyTask(), 1200L, 1200L);
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new FlyCheckTask(), 100L, 100L);
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                (new PlayerFlyExpansion(this)).register();
            }

        }

        public void onDisable() {
            Bukkit.getLogger().info("[PlayerTime] Disable");
            ConfigManager.saveConfigs();
        }
}
