package me.shark0822.combatSystem.stats;

import me.shark0822.combatSystem.commands.DebugMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.EnumMap;
import java.util.UUID;

public class DebugStatProvider implements StatProvider {
    private static final EnumMap<StatType, Double> baseStats = new EnumMap<>(StatType.class);
    private static final EnumMap<StatType, Double> tempStats = new EnumMap<>(StatType.class);

    private final UUID uuid;

    public DebugStatProvider(UUID uuid) {
        this.uuid = uuid;
    }

    public Entity getEntity() {
        return Bukkit.getEntity(uuid);
    }

    @Override
    public double getBaseStat(StatType type) {
        return baseStats.getOrDefault(type, 0.0);
    }

    public void setBaseStat(StatType type, double value) {
        double current = getBaseStat(type);

        if (DebugMode.isDebugMode()) {
            String name = getEntity() != null ? getEntity().getName() : "Unknown";
            Bukkit.getLogger().info("[CombatAPI][DebugStatProvider] " + name + "의 " + type + " 기본스탯이 " + current + " → " + value + "로 변경됨");
        }

        baseStats.put(type, value);
    }

    public void addBaseStat(StatType type, double value) {
        double current = getBaseStat(type);
        setBaseStat(type, current + value);
    }

    public void subtractBaseStat(StatType type, double value) {
        addBaseStat(type, -value);
    }

    @Override
    public double getTemporaryStat(StatType type) {
        return tempStats.getOrDefault(type, 0.0);
    }

    public void setTemporaryStat(StatType type, double value) {
        double current = getTemporaryStat(type);
        double updated = current + value;

        if (DebugMode.isDebugMode()) {
            String name = getEntity() != null ? getEntity().getName() : "Unknown";
            Bukkit.getLogger().info("[CombatAPI][DebugStatProvider] " + name + "의 " + type + " 임시스탯이 " + current + " → " + updated + "로 변경됨");
        }

        tempStats.put(type, value);
    }

    public void addTemporaryStat(StatType type, double value) {
        double current = getTemporaryStat(type);
        setTemporaryStat(type, current + value);
    }

    public void subtractTemporaryStat(StatType type, double value) {
        addTemporaryStat(type, -value);
    }

    public void resetBaseStat(StatType type) {
        baseStats.remove(type);
    }

    public void resetTemporaryStat(StatType type) {
        tempStats.remove(type);
    }

    public void resetAll() {
        baseStats.clear();
        tempStats.clear();
    }
}
