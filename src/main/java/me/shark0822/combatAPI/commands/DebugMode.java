package me.shark0822.combatAPI.commands;

import org.bukkit.Bukkit;

public class DebugMode {
    private static boolean debugMode = false;

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debug) {
        debugMode = debug;
        Bukkit.getLogger().info("[CombatAPI] [DebugMode] 디버그 모드가 " + debug + "로 설정되었습니다");
    }
}
