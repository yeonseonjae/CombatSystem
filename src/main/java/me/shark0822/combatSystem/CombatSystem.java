package me.shark0822.combatSystem;

import me.shark0822.combatSystem.commands.CombatSystemCommand;
import me.shark0822.combatSystem.events.DamageListener;
import me.shark0822.combatSystem.damage.receiver.DamageReceiver;
import me.shark0822.combatSystem.stats.StatUtil;
import me.shark0822.combatSystem.damage.receiver.DamageReceiverRegistry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class CombatSystem extends JavaPlugin {

    private static CombatSystem instance;

    @Override
    public void onEnable() {
        instance = this;

        StatUtil.setPlugin(this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);

        if (getCommand("combatsystem") != null) {
            CombatSystemCommand cmd = new CombatSystemCommand();
            getCommand("combatsystem").setExecutor(cmd);
            getCommand("combatsystem").setTabCompleter(cmd);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (LivingEntity entity : world.getLivingEntities()) {
                        DamageReceiver receiver = DamageReceiverRegistry.get(entity);
                        receiver.getStatModifierHandler().tickAll();
                    }
                }
            }
        }.runTaskTimer(this, 1L, 1L);
    }

    @Override
    public void onDisable() {
        getLogger().info("CombatSystem 플러그인이 비활성화 되었습니다.");
    }

    public static CombatSystem getInstance() {
        return instance;
    }
}
