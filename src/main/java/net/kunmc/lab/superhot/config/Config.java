package net.kunmc.lab.superhot.config;

import net.kunmc.lab.superhot.SuperHot;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public static boolean superHotEnabled;
    public static String timeFreezer;

    public static void load(boolean isReload) {
        SuperHot plugin = SuperHot.getPlugin();

        plugin.saveDefaultConfig();

        if (isReload) {
            plugin.reloadConfig();
        }

        FileConfiguration config = plugin.getConfig();

        superHotEnabled = config.getBoolean("superHotEnabled");
        timeFreezer = config.getString("timeFreezer");
    }
}
