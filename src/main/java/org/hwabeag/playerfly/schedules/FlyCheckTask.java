package org.hwabeag.playerfly.schedules;

import java.util.Iterator;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hwabeag.playerfly.config.ConfigManager;
import org.hwabeag.playerfly.inventorys.FlySettingGUI;

public class FlyCheckTask implements Runnable {
    FileConfiguration PlayerFlyConfig = ConfigManager.getConfig("playerfly");
    String Prefix = ChatColor.translateAlternateColorCodes('&', (String)Objects.requireNonNull(PlayerFlyConfig.getString("playerfly.prefix")));

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            if (Objects.equals(PlayerFlyConfig.getString("플라이." + name + ".관리"), "온")) {
                World world = player.getWorld();
                String worldname = world.getWorldFolder().getName();
                for (String key : Objects.requireNonNull(PlayerFlyConfig.getConfigurationSection("playerfly.noworld")).getKeys(false)) {
                    if (Objects.equals(key, worldname)) {
                        if (player.getGameMode() == GameMode.SURVIVAL) {
                            if (player.getAllowFlight()) {
                                player.setAllowFlight(false);
                            }
                            player.setFlying(false);
                        }
                        PlayerFlyConfig.set("플라이." + name + ".관리", "오프");
                        ConfigManager.saveConfigs();
                        player.sendMessage(Prefix + " 플라이 사용이 금지된 월드입니다.");
                    }
                }
            } else if (player.getGameMode() == GameMode.SURVIVAL) {
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                }
                player.setFlying(false);
            }
        }
    }
}

