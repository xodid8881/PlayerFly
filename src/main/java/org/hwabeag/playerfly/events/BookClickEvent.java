package org.hwabeag.playerfly.events;

import java.util.List;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.hwabeag.playerfly.config.ConfigManager;

public class BookClickEvent implements Listener {
    FileConfiguration PlayerFlyConfig = ConfigManager.getConfig("playerfly");
    String Prefix;

    public BookClickEvent() {
        this.Prefix = ChatColor.translateAlternateColorCodes('&', (String)Objects.requireNonNull(this.PlayerFlyConfig.getString("playerfly.prefix")));
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();
        if (player.getItemInHand().getItemMeta() != null) {
            String itemname = player.getItemInHand().getItemMeta().getDisplayName();
            List<String> itemLore = player.getInventory().getItemInMainHand().getItemMeta().getLore();
            if (player.getItemInHand().getType() == Material.BOOK && itemname.equals(ChatColor.translateAlternateColorCodes('&', "&a&l[플라이권]"))) {
                String tier = (String)((List)Objects.requireNonNull(itemLore)).get(0);
                String intStr = tier.replaceAll("[^0-9]", "");
                int TimeNumber = this.PlayerFlyConfig.getInt("플라이." + name + ".남은시간");
                this.PlayerFlyConfig.set("플라이." + name + ".남은시간", TimeNumber + Integer.parseInt(intStr));
                ConfigManager.saveConfigs();
                String var10001 = this.Prefix;
                player.sendMessage(var10001 + " 플라이권을 사용하여 " + Integer.parseInt(intStr) + "분 지급받았습니다.");
                TimeNumber = this.PlayerFlyConfig.getInt("플라이." + name + ".남은시간");
                player.sendMessage(this.Prefix + " 나의 플라이 남은시간: " + TimeNumber + "분");
                ItemStack item = player.getInventory().getItemInMainHand();
                item.setAmount(item.getAmount() - 1);
            }

        }
    }
}
