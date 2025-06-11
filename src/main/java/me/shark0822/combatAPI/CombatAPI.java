package me.shark0822.combatAPI;

import me.shark0822.combatAPI.commands.CombatAPICommand;
import me.shark0822.combatAPI.events.DamageListener;
import me.shark0822.combatAPI.stats.StatUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class CombatAPI extends JavaPlugin {

    private static CombatAPI instance;

    @Override
    public void onEnable() {
        instance = this;

        StatUtil.setPlugin(this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);

        if (getCommand("combatapi") != null) {
            CombatAPICommand cmd = new CombatAPICommand();
            getCommand("combatapi").setExecutor(cmd);
            getCommand("combatapi").setTabCompleter(cmd);
        }

        getLogger().info("CombatAPI 플러그인이 활성화 되었습니다.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CombatAPI 플러그인이 비활성화 되었습니다.");
    }

    public static CombatAPI getInstance() {
        return instance;
    }
}
