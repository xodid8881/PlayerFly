package org.hwabeag.playerfly.schedules;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hwabeag.playerfly.config.ConfigManager;

public class PlayerFlyTask implements Runnable {
    FileConfiguration PlayerFlyConfig = ConfigManager.getConfig("playerfly");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(PlayerFlyConfig.getString("playerfly.prefix")));

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            if (Objects.equals(PlayerFlyConfig.getString("플라이." + name + ".관리"), "온")) {
                if (PlayerFlyConfig.getBoolean("playerfly.fly-infinity")) {
                    player.sendActionBar(Prefix + ChatColor.translateAlternateColorCodes('&',"&7당신은 플라이를 이용중입니다."));
                } else {
                    if (PlayerFlyConfig.getInt("플라이." + name + ".남은시간") > 0) {
                        int TimeNumber = PlayerFlyConfig.getInt("플라이." + name + ".남은시간");
                        PlayerFlyConfig.set("플라이." + name + ".남은시간", TimeNumber - 1);
                        ConfigManager.saveConfigs();
                        player.sendActionBar(Prefix + ChatColor.translateAlternateColorCodes('&',"&7남은 플라이: &a" + TimeNumber + " &7분"));
                    } else {
                        PlayerFlyConfig.set("플라이." + name + ".남은시간", 0);
                        PlayerFlyConfig.set("플라이." + name + ".관리", "오프");
                        ConfigManager.saveConfigs();
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        String s = ChatColor.translateAlternateColorCodes('&', "&a&l[플라이]");
                        String s1 = ChatColor.translateAlternateColorCodes('&', "&a&l- &7당신은 플라이를 모두 소모하였습니다.");
                        player.sendTitle(s, s1);
                    }
                }
            }
        }
    }
}