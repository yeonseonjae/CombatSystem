package me.shark0822.combatSystem.stats.provider;

import me.shark0822.combatSystem.stats.StatType;

import java.util.Arrays;
import java.util.List;

public class StatAggregator implements StatProvider {

    private final List<StatProvider> sources;

    public StatAggregator(StatProvider... providers) {
        this.sources = Arrays.asList(providers);
    }

    @Override
    public double getBaseStat(StatType type) {
        return sources.stream().mapToDouble(p -> p.getBaseStat(type)).sum();
    }

    @Override
    public double getTemporaryStat(StatType type) {
        return sources.stream().mapToDouble(p -> p.getTemporaryStat(type)).sum();
    }

    public List<StatProvider> getProviders() {
        return sources;
    }
}
