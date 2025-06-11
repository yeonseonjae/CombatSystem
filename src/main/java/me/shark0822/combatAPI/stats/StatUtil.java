package me.shark0822.combatAPI.stats;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class StatUtil {
    private static JavaPlugin plugin;

    public static void setPlugin(JavaPlugin plugin) {
        StatUtil.plugin = plugin;
    }

    public static NamespacedKey getKey(StatType type) {
        return new NamespacedKey(plugin, type.name().toLowerCase());
    }
}
