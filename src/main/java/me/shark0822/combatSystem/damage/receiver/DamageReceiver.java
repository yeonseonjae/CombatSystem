package me.shark0822.combatSystem.damage.receiver;

import me.shark0822.combatSystem.damage.type.DamageType;
import me.shark0822.combatSystem.stats.*;
import me.shark0822.combatSystem.stats.modifier.StatModifierHandler;
import me.shark0822.combatSystem.stats.provider.DebugStatProvider;
import me.shark0822.combatSystem.stats.provider.StatAggregator;
import me.shark0822.combatSystem.stats.provider.StatProvider;
import me.shark0822.combatSystem.stats.provider.StatProviderManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DamageReceiver {

    private final StatAggregator stats;
    private final StatModifierHandler modifierHandler = new StatModifierHandler();
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

    public StatModifierHandler getStatModifierHandler() {
        return modifierHandler;
    }

    public double getFinalStat(StatType stat) {
        double base = getBaseStat(stat);
        double mod = modifierHandler.getModifierTotal(stat);
        return base + mod;
    }

    public double getBaseStat(StatType type) {
        return stats.getBaseStat(type);
    }

    public double getTotalDamageReduction() {
        return getFinalStat(StatType.DAMAGE_REDUCTION);
    }

    public double getTotalReductionIgnored() {
        return getFinalStat(StatType.REDUCTION_IGNORED);
    }

    public double getTakenDamageIncreaseGeneral() {
        return getFinalStat(StatType.TAKEN_DAMAGE_INCREASE);
    }

    public double getTakenDamageIncreaseElemental(DamageType type) {
        if (!isElemental(type)) return 0.0;

        StatType stat = StatType.valueOf("TAKEN_DAMAGE_INCREASE_" + type.name());
        return getFinalStat(stat);
    }

    public double getTakenDamageMultiplier(DamageType type) {
        double general = getTakenDamageIncreaseGeneral();
        double elemental = getTakenDamageIncreaseElemental(type);

        return (1.0 + general) * (1.0 + elemental);
    }

    public double getDamageIncreaseGeneral() {
        return getFinalStat(StatType.DAMAGE_INCREASE);
    }

    public double getDamageIncreaseElemental(DamageType type) {
        if (!isElemental(type)) return 0.0;

        StatType specific = StatType.valueOf("DAMAGE_INCREASE_" + type.name());
        return getFinalStat(specific);
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
}
