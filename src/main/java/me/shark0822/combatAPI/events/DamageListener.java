package me.shark0822.combatAPI.events;

import me.shark0822.combatAPI.damage.*;
import me.shark0822.combatAPI.stats.DamageReceiver;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (DamageApplier.isProcessing(entity)) return;

        double baseDamage = event.getDamage();

        DamageType damageType = DamageType.NONE;
        AttackType attackType = getAttackType(event);

        Entity damager = null;
        DamageReceiver damagerReceiver = null;

        if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            if (damageByEntityEvent.getDamager() instanceof Projectile projectile) {
                if (projectile.getShooter() == null) return;
                if (projectile.getShooter() instanceof Entity) {
                    damager = (Entity) projectile.getShooter();
                    damagerReceiver = new DamageReceiver(damager.getUniqueId());
                }
            } else {
                damager = damageByEntityEvent.getDamager();
                damagerReceiver = new DamageReceiver(damager.getUniqueId());
            }
        }

        DamageInstance instance = new DamageInstance(baseDamage, damageType, attackType);
        DamageReceiver targetReceiver = new DamageReceiver(entity.getUniqueId());

        double finalDamage = baseDamage;

        PreviousDamageEvent preEvent = new PreviousDamageEvent(
                damager,
                entity,
                baseDamage,
                finalDamage,
                damagerReceiver,
                targetReceiver,
                instance
        );
        Bukkit.getPluginManager().callEvent(preEvent);

        if (preEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        finalDamage = DamageCalculator.calculateFinalDamage(preEvent.getDamageInstance(), damagerReceiver, targetReceiver);

        event.setDamage(finalDamage);
    }

    private AttackType getAttackType(EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();

        return switch (cause) {
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> AttackType.PHYSICAL;
            case PROJECTILE -> AttackType.PROJECTILE;
            case MAGIC, DRAGON_BREATH, LIGHTNING, POISON, SONIC_BOOM, WITHER -> AttackType.MAGIC;
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> AttackType.AREA;
            case CAMPFIRE, CONTACT, CRAMMING, DROWNING, DRYOUT, FALL, FALLING_BLOCK, FIRE, FIRE_TICK, FLY_INTO_WALL,
                 FREEZE, HOT_FLOOR, LAVA, MELTING, STARVATION, SUFFOCATION, VOID, WORLD_BORDER ->
                    AttackType.ENVIRONMENTAL;
            case THORNS -> AttackType.THORNS;
            case CUSTOM, KILL, SUICIDE -> AttackType.CUSTOM;
            default -> AttackType.NONE;
        };
    }
}
