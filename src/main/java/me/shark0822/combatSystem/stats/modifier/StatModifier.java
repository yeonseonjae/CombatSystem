package me.shark0822.combatSystem.stats.modifier;

import me.shark0822.combatSystem.commands.DebugMode;
import me.shark0822.combatSystem.stats.StatType;
import org.bukkit.Bukkit;

public class StatModifier {
    private final String id;
    private final StatType stat;
    private final float value;
    private final long durationTicks;
    private long remainingTicks;
    private final boolean isNegative;

    public StatModifier(String id, StatType stat, float value, long durationTicks, boolean isNegative) {
        this.id = id;
        this.stat = stat;
        this.value = value;
        this.durationTicks = durationTicks;
        this.remainingTicks = durationTicks;
        this.isNegative = isNegative;
    }

    public void tick() {
        remainingTicks--;
    }

    public boolean isExpired() {
        return remainingTicks <= 0;
    }

    public float getEffectiveValue() {
        return value;
    }

    public StatType getStat() {
        return stat;
    }

    public String getId() {
        return id;
    }

    public boolean isNegative() {
        return isNegative;
    }

    protected void onApply() {
        if (DebugMode.isDebugMode()) {
            Bukkit.getLogger().info("[CombatSystem][StatModifier] 적용됨: " +
                    "ID=" + id + ", Stat=" + stat + ", 값=" + value + ", 지속시간=" + durationTicks + "tick" +
                    (isNegative ? " (디버프)" : " (버프)"));
        }
    }

    protected void onExpire() {
        if (DebugMode.isDebugMode()) {
            Bukkit.getLogger().info("[CombatSystem][StatModifier] 만료됨: " +
                    "ID=" + id + ", Stat=" + stat + ", 적용값=" + value);
        }
    }
}
