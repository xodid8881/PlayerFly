package org.hwabeag.playerfly.commands;

import java.util.Iterator;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hwabeag.playerfly.config.ConfigManager;
import org.hwabeag.playerfly.inventorys.FlySettingGUI;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {
    FileConfiguration PlayerFlyConfig = ConfigManager.getConfig("playerfly");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(PlayerFlyConfig.getString("playerfly.prefix")));

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 0) {
                if (p.isOp()) {
                    p.sendMessage(Prefix + " /플라이관리 정보 [닉네임] - 유저의 정보를 확인합니다.");
                    p.sendMessage(Prefix + " /플라이관리 [지급, 제외, 설정] [닉네임] [분] - 유저의 플라이 시간을 관리합니다.");
                    p.sendMessage(Prefix + " /플라이관리 무제한 - 플라이 무제한을 활성화 혹 비활성화 합니다.");
                    p.sendMessage(Prefix + " /플라이관리 리로드 - 콘피그 정보를 리로드 합니다.");
                }

                World world = p.getWorld();
                String worldname = world.getWorldFolder().getName();
                for (String key : Objects.requireNonNull(PlayerFlyConfig.getConfigurationSection("playerfly.noworld")).getKeys(false)) {
                    if (Objects.equals(key, worldname)){
                        p.sendMessage(this.Prefix + " 플라이 사용이 금지된 월드입니다.");
                        return true;
                    }
                }
                FlySettingGUI inv = new FlySettingGUI(p);
                inv.open(p);
                return true;
            } else if (p.isOp()) {
                if (args[0].equalsIgnoreCase("정보")) {
                    if (PlayerFlyConfig.getString("플라이." + args[1] + ".남은시간") == null) {
                        p.sendMessage(Prefix + " 해당 닉네임의 정보는 존재하지 않습니다.");
                    } else {
                        String Setting = PlayerFlyConfig.getString("플라이." + args[1] + ".관리");
                        String TimeNumber = PlayerFlyConfig.getString("플라이." + args[1] + ".남은시간");
                        p.sendMessage(Prefix + " 해당 유저의 플라이 남은 시간: " + TimeNumber + " 분 입니다.");
                        if (Objects.equals(Setting, "온")) {
                            p.sendMessage(Prefix + " 해당 유저는 플라이를 사용중입니다.");
                        } else if (Objects.equals(Setting, "오프")) {
                            p.sendMessage(Prefix + " 해당 유저는 플라이를 사용하지 않고 있습니다.");
                        }

                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("지급")) {
                    if (args.length == 1) {
                        p.sendMessage(Prefix + " /플라이관리 [지급, 제외, 설정] [닉네임] [분] - 유저의 플라이 시간을 관리합니다.");
                    } else {
                        if (PlayerFlyConfig.getString("플라이." + args[1] + ".남은시간") != null) {
                            int TimeNumber = PlayerFlyConfig.getInt("플라이." + args[1] + ".남은시간");
                            PlayerFlyConfig.set("플라이." + args[1] + ".남은시간", TimeNumber + Integer.parseInt(args[2]));
                            ConfigManager.saveConfigs();
                            p.sendMessage(Prefix + " " + args[1] + " 님에게 플라이 시간을 " + args[2] + "분 지급했습니다.");
                            getPlayerSettingMsg("지급", args[1], Integer.parseInt(args[2]));
                        } else {
                            p.sendMessage(Prefix + " " + args[1] + " 닉네임의 유저가 존재하지 않습니다.");
                        }

                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("제외")) {
                    if (args.length == 1) {
                        p.sendMessage(Prefix + " /플라이관리 [지급, 제외, 설정] [닉네임] [분] - 유저의 플라이 시간을 관리합니다.");
                    } else {
                        if (this.PlayerFlyConfig.getString("플라이." + args[1] + ".남은시간") != null) {
                            int TimeNumber = PlayerFlyConfig.getInt("플라이." + args[1] + ".남은시간");
                            PlayerFlyConfig.set("플라이." + args[1] + ".남은시간", TimeNumber - Integer.parseInt(args[2]));
                            ConfigManager.saveConfigs();
                            p.sendMessage(Prefix + " " + args[1] + " 님의 플라이 시간을 " + args[2] + "분 제외했습니다.");
                            getPlayerSettingMsg("지급", args[1], Integer.parseInt(args[2]));
                        } else {
                            p.sendMessage(Prefix + " " + args[1] + " 닉네임의 유저가 존재하지 않습니다.");
                        }

                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("설정")) {
                    if (args.length == 1) {
                        p.sendMessage(this.Prefix + " /플라이관리 [지급, 제외, 설정] [닉네임] [분] - 유저의 플라이 시간을 관리합니다.");
                    } else {
                        if (PlayerFlyConfig.getString("플라이." + args[1] + ".남은시간") != null) {
                            PlayerFlyConfig.set("플라이." + args[1] + ".남은시간", Integer.parseInt(args[2]));
                            ConfigManager.saveConfigs();
                            p.sendMessage(Prefix + " " + args[1] + " 님의 시간을 " + args[2] + "분으로 설정했습니다.");
                            getPlayerSettingMsg("지급", args[1], Integer.parseInt(args[2]));
                        } else {
                            p.sendMessage(Prefix + " " + args[1] + " 닉네임의 유저가 존재하지 않습니다.");
                        }

                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("무제한")) {
                    if (PlayerFlyConfig.getBoolean("playerfly.fly-infinity")) {
                        PlayerFlyConfig.set("playerfly.fly-infinity", false);
                    } else {
                        PlayerFlyConfig.set("playerfly.fly-infinity", true);
                    }
                    p.sendMessage(Prefix + " 서버 내 무제한 플라이를 비활성화 했습니다.");
                    return true;
                }
                if (args[0].equalsIgnoreCase("리로드")) {
                    ConfigManager.reloadConfigs();
                    p.sendMessage(Prefix + " 플레이타임 콘피그가 리로드 되었습니다.");
                    return true;
                }
                p.sendMessage(Prefix + " /플라이관리 정보 [닉네임] - 유저의 정보를 확인합니다.");
                p.sendMessage(Prefix + " /플라이관리 [지급, 제외, 설정] [닉네임] [분] - 유저의 플라이 시간을 관리합니다.");
                p.sendMessage(Prefix + " /플라이관리 무제한 - 플라이 무제한을 활성화 혹 비활성화 합니다.");
                p.sendMessage(Prefix + " /플라이관리 리로드 - 콘피그 정보를 리로드 합니다.");
                return true;
            } else {
                p.sendMessage(Prefix + " 당신은 권한이 없습니다.");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("지급")) {
            if (args.length == 1) {
                Bukkit.getConsoleSender().sendMessage(Prefix + " /플라이관리 지급 [닉네임] [분] - 유저의 플라이 시간을 관리합니다.");
            } else {
                if (PlayerFlyConfig.getString("플라이." + args[1] + ".남은시간") != null) {
                    int TimeNumber = PlayerFlyConfig.getInt("플라이." + args[1] + ".남은시간");
                    PlayerFlyConfig.set("플라이." + args[1] + ".남은시간", TimeNumber + Integer.parseInt(args[2]));
                    ConfigManager.saveConfigs();
                    Bukkit.getConsoleSender().sendMessage(Prefix + " " + args[1] + " 님에게 플라이 시간을 " + args[2] + "분 지급했습니다.");
                    getPlayerSettingMsg("지급", args[1], Integer.parseInt(args[2]));
                }

            }
            return true;
        } else {
            Bukkit.getConsoleSender().sendMessage(Prefix + " /플라이관리 설정 [닉네임] [분] - 유저의 플라이 시간을 관리합니다.");
            return true;
        }
    }

    public void getPlayerSettingMsg(String type, String name, int time) {
        Player player;
        if (Objects.equals(type, "지급") && Bukkit.getServer().getPlayerExact(name) != null) {
            player = Bukkit.getServer().getPlayerExact(name);
            player.sendMessage(Prefix + " 관리진이 당신에세 플라이 시간을 지급했습니다.");
            player.sendMessage(Prefix + " 지급된 시간: " + time + " 분");
        }

        if (Objects.equals(type, "제외") && Bukkit.getServer().getPlayerExact(name) != null) {
            player = Bukkit.getServer().getPlayerExact(name);
            player.sendMessage(Prefix + " 관리진이 당신의 플라이 시간을 제외했습니다.");
            player.sendMessage(Prefix + " 제외된 시간: " + time + " 분");
        }

        if (Objects.equals(type, "설정") && Bukkit.getServer().getPlayerExact(name) != null) {
            player = Bukkit.getServer().getPlayerExact(name);
            player.sendMessage(Prefix + " 관리진이 당신의 플라이 시간을 설정했습니다.");
            player.sendMessage(Prefix + " 설정된 시간: " + time + " 분");
        }

    }
}
