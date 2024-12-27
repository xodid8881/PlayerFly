package org.hwabeag.playerfly.inventorys;

import java.util.ArrayList;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.hwabeag.playerfly.config.ConfigManager;

public class FlySettingGUI implements Listener {
    private final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 9, "플라이");
    FileConfiguration PlayerFlyConfig = ConfigManager.getConfig("playerfly");

    private ItemStack getHead(String name) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        if (Objects.equals(PlayerFlyConfig.getString("플라이." + name + ".관리"), "온")) {
            skull.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&l" + name + " &7님의 플라이 &a사용중"));
            skull.setOwner(name);
        } else if (Objects.equals(PlayerFlyConfig.getString("플라이." + name + ".관리"), "오프")) {
            skull.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&l" + name + " &7님의 플라이 &c중지중"));
            skull.setOwner(name);
        }

        String TimeNumber = PlayerFlyConfig.getString("플라이." + name + ".남은시간");
        ArrayList<String> loreList = new ArrayList();
        if (PlayerFlyConfig.getBoolean("playerfly.fly-infinity")) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', "&7&l- &a&l무제한 &7사용가능"));
        } else {
            loreList.add(ChatColor.translateAlternateColorCodes('&', "&7&l- &a&l" + TimeNumber + " &7분 동안 사용가능"));
        }
        loreList.add(ChatColor.translateAlternateColorCodes('&', "&7&l- &a&l클릭시 플라이를 활성화 혹 비활성화 할 수 있습니다."));
        skull.setLore(loreList);
        item.setItemMeta(skull);
        return item;
    }

    private void initItemSetting(Player p) {
        String name = p.getName();
        inv.setItem(4, getHead(name));
    }

    public FlySettingGUI(Player p) {
        initItemSetting(p);
    }

    public void open(Player player) {
        player.openInventory(inv);
    }
}
