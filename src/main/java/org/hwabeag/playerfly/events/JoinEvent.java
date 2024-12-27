package org.hwabeag.playerfly.events;

import java.util.Objects;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.hwabeag.playerfly.config.ConfigManager;

public class JoinEvent implements Listener {
    FileConfiguration PlayerFlyConfig = ConfigManager.getConfig("playerfly");

    public JoinEvent() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = ((Player)Objects.requireNonNull(player)).getName();
        if (this.PlayerFlyConfig.getString("플라이." + name + ".남은시간") == null) {
            this.PlayerFlyConfig.addDefault("플라이." + name + ".남은시간", 0);
            this.PlayerFlyConfig.addDefault("플라이." + name + ".관리", "오프");
            ConfigManager.saveConfigs();
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.setFlying(false);
            player.setAllowFlight(false);
            this.PlayerFlyConfig.set("플라이." + name + ".관리", "오프");
            ConfigManager.saveConfigs();
        }

    }
}

