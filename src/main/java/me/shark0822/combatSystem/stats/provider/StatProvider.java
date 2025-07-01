package me.shark0822.combatSystem.stats.provider;

import me.shark0822.combatSystem.stats.StatType;

public interface StatProvider {
    double getBaseStat(StatType statType);
    double getTemporaryStat(StatType statType);
}
