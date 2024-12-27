package org.hwabeag.playerfly.events;

import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.hwabeag.playerfly.config.ConfigManager;

public class InvClickEvent implements Listener {
    FileConfiguration PlayerFlyConfig = ConfigManager.getConfig("playerfly");
    String Prefix= ChatColor.translateAlternateColorCodes('&', (String)Objects.requireNonNull(PlayerFlyConfig.getString("playerfly.prefix")));

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null) {
            if (e.getCurrentItem() != null && ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("플라이")) {
                Player player = (Player)e.getWhoClicked();
                String name = player.getName();
                if (e.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                    e.setCancelled(true);
                    String fly = PlayerFlyConfig.getString("플라이." + name + ".관리");
                    if (Objects.equals(fly, "오프")) {
                        if (!PlayerFlyConfig.getBoolean("playerfly.fly-infinity")) {
                            if (PlayerFlyConfig.getInt("플라이." + name + ".남은시간") <= 0) {
                                player.sendMessage(Prefix + " 당신은 보유한 시간이 없습니다.");
                                return;
                            }
                        }
                        if (player.getGameMode() == GameMode.SURVIVAL && !player.getAllowFlight()) {
                            player.setAllowFlight(true);
                        }
                        PlayerFlyConfig.set("플라이." + name + ".관리", "온");
                        ConfigManager.saveConfigs();
                        player.closeInventory();
                        player.sendMessage(Prefix + " 플라이를 사용합니다.");
                    } else if (Objects.equals(fly, "온")) {
                        if (player.getGameMode() == GameMode.SURVIVAL && player.getAllowFlight()) {
                            player.setAllowFlight(false);
                        }
                        player.setFlying(false);
                        this.PlayerFlyConfig.set("플라이." + name + ".관리", "오프");
                        ConfigManager.saveConfigs();
                        player.closeInventory();
                        player.sendMessage(Prefix + " 플라이 사용을 중지합니다.");
                    }
                }
            }

        }
    }
}
