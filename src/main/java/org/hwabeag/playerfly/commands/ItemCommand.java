package org.hwabeag.playerfly.commands;

import java.util.ArrayList;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hwabeag.playerfly.config.ConfigManager;
import org.jetbrains.annotations.NotNull;

public class ItemCommand implements CommandExecutor {
    FileConfiguration PlayerFlyConfig = ConfigManager.getConfig("playerfly");
    String Prefix;

    public ItemCommand() {
        this.Prefix = ChatColor.translateAlternateColorCodes('&', (String)Objects.requireNonNull(this.PlayerFlyConfig.getString("playerfly.prefix")));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            if (p.isOp()) {
                if (args.length == 0) {
                    p.sendMessage(this.Prefix + " /플라이권 지급 [닉네임] [분] - 유저에게 플라이권을 지급합니다.");
                    p.sendMessage(this.Prefix + " /플라이권 불러오기 [분] - 플라이권을 불러옵니다.");
                    return true;
                } else if (args[0].equalsIgnoreCase("불러오기")) {
                    ItemStack item = new ItemStack(Material.BOOK, 1, (short)3);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&l[플라이권]"));
                    ArrayList<String> loreList = new ArrayList();
                    loreList.add(ChatColor.translateAlternateColorCodes('&', "&f&l- 사용시 플라이 &a&l" + args[1] + " &f분 추가"));
                    loreList.add(ChatColor.translateAlternateColorCodes('&', "&f&l- &a&l땅에 클릭시 사용됩니다."));
                    meta.setLore(loreList);
                    item.setItemMeta(meta);
                    p.getInventory().addItem(new ItemStack[]{item});
                    p.sendMessage(this.Prefix + " 플라이권을 불러왔습니다.");
                    return true;
                } else {
                    if (args[0].equalsIgnoreCase("지급")) {
                        if (args.length == 1) {
                            p.sendMessage(this.Prefix + " /플라이권 지급 [닉네임] [분] - 유저에게 플라이권을 지급합니다.");
                            return true;
                        }

                        if (Bukkit.getServer().getPlayerExact(args[1]) != null) {
                            Player player = Bukkit.getServer().getPlayerExact(args[1]);
                            ItemStack item = new ItemStack(Material.BOOK, 1, (short)3);
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&l[플라이권]"));
                            ArrayList<String> loreList = new ArrayList();
                            loreList.add(ChatColor.translateAlternateColorCodes('&', "&f&l- 사용시 플라이 &a&l" + args[2] + " &f분 추가"));
                            loreList.add(ChatColor.translateAlternateColorCodes('&', "&f&l- &a&l땅에 클릭시 사용됩니다."));
                            meta.setLore(loreList);
                            item.setItemMeta(meta);
                            player.getInventory().addItem(new ItemStack[]{item});
                            player.sendMessage(this.Prefix + " 플라이권을 불러왔습니다.");
                            return true;
                        }
                    }

                    p.sendMessage(this.Prefix + " /플라이권 지급 [닉네임] [분] - 유저에게 플라이권을 지급합니다.");
                    p.sendMessage(this.Prefix + " /플라이권 불러오기 [분] - 플라이권을 불러옵니다.");
                    return true;
                }
            } else {
                p.sendMessage(this.Prefix + " 당신은 권한이 없습니다.");
                return true;
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(this.Prefix + " 인게임에서만 사용이 가능합니다.");
            return true;
        }
    }
}
