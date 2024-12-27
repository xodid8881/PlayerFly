package org.hwabeag.playerfly.config;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigMaker {
    private final File file;
    private FileConfiguration config;

    public ConfigMaker(String path, String fileName) {
        this.file = new File(path + "/" + fileName);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void saveConfig() {
        if (this.config != null) {
            try {
                this.config.save(this.file);
            } catch (Exception var2) {
                Exception e = var2;
                e.printStackTrace();
            }

        }
    }

    public boolean exists() {
        return this.file != null && this.file.exists();
    }

    public void reloadConfig() {
        if (this.exists()) {
            this.config = YamlConfiguration.loadConfiguration(this.file);
        }
    }
}
