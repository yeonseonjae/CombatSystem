package me.shark0822.combatSystem.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class HealthManager {

    public static void applyDamage(LivingEntity entity, double damage) {
        double absorption = entity.getAbsorptionAmount();
        double remainingDamage = damage;

        if (absorption > 0) {
            double absorbed = Math.min(absorption, remainingDamage);
            entity.setAbsorptionAmount(absorption - absorbed);
            remainingDamage -= absorbed;
        }

        if (remainingDamage > 0) {
            double newHealth = Math.max(0.0, entity.getHealth() - remainingDamage);
            entity.setHealth(newHealth);
        }
    }

    public static void applyDamage(LivingEntity victim, Entity attacker, double damage) {
        double absorption = victim.getAbsorptionAmount();
        double remainingDamage = damage;

        if (absorption > 0) {
            double absorbed = Math.min(absorption, remainingDamage);
            victim.setAbsorptionAmount(absorption - absorbed);
            remainingDamage -= absorbed;
        }

        if (remainingDamage > 0) {
            double newHealth = Math.max(0.0, victim.getHealth() - remainingDamage);
            victim.setHealth(newHealth);
        }
        victim.damage(1e-6, attacker);
    }
}
