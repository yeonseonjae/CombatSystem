package me.shark0822.combatSystem;

import me.shark0822.combatSystem.commands.CombatAPICommand;
import me.shark0822.combatSystem.events.DamageListener;
import me.shark0822.combatSystem.stats.StatUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class CombatSystem extends JavaPlugin {

    private static CombatSystem instance;

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

        getLogger().info("CombatSystem 플러그인이 활성화 되었습니다.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CombatSystem 플러그인이 비활성화 되었습니다.");
    }

    public static CombatSystem getInstance() {
        return instance;
    }
}
