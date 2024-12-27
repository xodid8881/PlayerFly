package org.hwabeag.playerfly.config;

import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.configuration.file.FileConfiguration;
import org.hwabeag.playerfly.PlayerFly;

public class ConfigManager {
    private static final PlayerFly plugin = PlayerFly.getPlugin();
    private static final HashMap<String, ConfigMaker> configSet = new HashMap();

    public ConfigManager() {
        String path = plugin.getDataFolder().getAbsolutePath();
        configSet.put("playerfly", new ConfigMaker(path, "PlayerFly.yml"));
        this.loadSettings();
        saveConfigs();
    }

    public static void reloadConfigs() {
        Iterator var0 = configSet.keySet().iterator();

        while(var0.hasNext()) {
            String key = (String)var0.next();
            plugin.getLogger().info(key);
            ((ConfigMaker)configSet.get(key)).reloadConfig();
        }

    }

    public static void saveConfigs() {
        Iterator var0 = configSet.keySet().iterator();

        while(var0.hasNext()) {
            String key = (String)var0.next();
            ((ConfigMaker)configSet.get(key)).saveConfig();
        }

    }

    public static FileConfiguration getConfig(String fileName) {
        return ((ConfigMaker)configSet.get(fileName)).getConfig();
    }

    public void loadSettings() {
        FileConfiguration PlayerFlyConfig = getConfig("playerfly");
        PlayerFlyConfig.options().copyDefaults(true);
        PlayerFlyConfig.addDefault("playerfly.prefix", "&a&l[플라이]&7");
        PlayerFlyConfig.addDefault("playerfly.noworld.test", "test");
        PlayerFlyConfig.addDefault("playerfly.fly-infinity", true);
    }
}
