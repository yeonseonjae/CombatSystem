package me.shark0822.combatSystem.stats;

public interface StatProvider {
    double getBaseStat(StatType statType);
    double getTemporaryStat(StatType statType);
}
