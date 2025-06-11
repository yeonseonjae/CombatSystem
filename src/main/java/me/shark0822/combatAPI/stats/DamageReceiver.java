package me.shark0822.combatAPI.stats;

import me.shark0822.combatAPI.damage.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DamageReceiver {

    private final StatAggregator stats;
    private final List<StatProvider> providers;
    public Entity entity;

    public DamageReceiver(UUID uuid) {
        this.stats = StatProviderManager.createAggregator(uuid);
        this.providers = new ArrayList<>(stats.getProviders());
        this.entity = Bukkit.getEntity(uuid);
    }

    public Entity getEntity() {
        return entity;
    }

    public double getBaseStat(StatType type) {
        return stats.getBaseStat(type);
    }

    public double getTemporaryStat(StatType type) {
        return stats.getTemporaryStat(type);
    }

    public double getTotalDamageReduction() {
        double base = getBaseStat(StatType.DAMAGE_REDUCTION);
        double temp = getTemporaryStat(StatType.DAMAGE_REDUCTION);
        return base + temp;
    }

    public double getTotalReductionIgnored() {
        double base = getBaseStat(StatType.REDUCTION_IGNORED);
        double temp = getTemporaryStat(StatType.REDUCTION_IGNORED);
        return base + temp;
    }

    public double getTakenDamageIncreaseGeneral() {
        double base = getBaseStat(StatType.TAKEN_DAMAGE_INCREASE);
        double temp = getTemporaryStat(StatType.TAKEN_DAMAGE_INCREASE);
        return base + temp;
    }

    public double getTakenDamageIncreaseElemental(DamageType type) {
        if (!isElemental(type)) return 0.0;

        StatType stat = StatType.valueOf("TAKEN_DAMAGE_INCREASE_" + type.name());
        double base = getBaseStat(stat);
        double temp = getTemporaryStat(stat);
        return base + temp;
    }

    public double getTakenDamageMultiplier(DamageType type) {
        double general = getTakenDamageIncreaseGeneral();
        double elemental = getTakenDamageIncreaseElemental(type);

        return (1.0 + general) * (1.0 + elemental);
    }

    public double getDamageIncreaseGeneral() {
        double base = getBaseStat(StatType.DAMAGE_INCREASE);
        double temp = getTemporaryStat(StatType.DAMAGE_INCREASE);
        return base + temp;
    }

    public double getDamageIncreaseElemental(DamageType type) {
        if (!isElemental(type)) return 0.0;

        StatType specific = StatType.valueOf("DAMAGE_INCREASE_" + type.name());
        double base = getBaseStat(specific);
        double temp = getTemporaryStat(specific);
        return base + temp;
    }

    public double getDamageMultiplier(DamageType type) {
        double elemental = getDamageIncreaseElemental(type);
        double general = getDamageIncreaseGeneral();

        return (1.0 + elemental) * (1.0 + general);
    }

    public boolean isElemental(DamageType type) {
        return switch (type) {
            case FLAME, FROST, GALE, ELECTRIC, DARKNESS, DIVINE -> true;
            default -> false;
        };
    }

    public void setBaseStat(StatType type, double value) {
        for (StatProvider provider : providers) {
            if (provider instanceof DebugStatProvider debugProvider) {
                debugProvider.setBaseStat(type, value);
                return;
            }
        }
        throw new UnsupportedOperationException("Setter 지원하지 않는 StatProvider입니다.");
    }

    public void setTemporaryStat(StatType type, double value) {
        for (StatProvider provider : providers) {
            if (provider instanceof DebugStatProvider debugProvider) {
                debugProvider.setTemporaryStat(type, value);
                return;
            }
        }
        throw new UnsupportedOperationException("Setter 지원하지 않는 StatProvider입니다.");
    }
}
