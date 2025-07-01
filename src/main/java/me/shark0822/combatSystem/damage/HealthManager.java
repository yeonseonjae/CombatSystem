package me.shark0822.combatSystem.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class HealthManager {

    public static void applyDamage(LivingEntity victim, Entity attacker, double damage) {
        DamageApplier.markProcessing(victim);

        double absorption = victim.getAbsorptionAmount();
        double remainingDamage = damage;

        victim.damage(1e-6, attacker);

        if (absorption > 0) {
            double absorbed = Math.min(absorption, remainingDamage);
            victim.setAbsorptionAmount(absorption - absorbed);
            remainingDamage -= absorbed;
        }

        if (remainingDamage > 0) {
            double newHealth = Math.max(0.0, victim.getHealth() - remainingDamage);
            victim.setHealth(newHealth);
        }

        DamageApplier.unmarkProcessing(victim);
    }
}
