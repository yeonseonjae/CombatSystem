package me.shark0822.combatAPI.stats;

public interface StatProvider {
    double getBaseStat(StatType statType);
    double getTemporaryStat(StatType statType);
}
