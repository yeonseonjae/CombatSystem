package me.shark0822.combatSystem.events;

import me.shark0822.combatSystem.damage.*;
import me.shark0822.combatSystem.damage.calculator.StatDamageCalculator;
import me.shark0822.combatSystem.damage.calculator.VanillaDamageCalculator;
import me.shark0822.combatSystem.damage.type.AttackType;
import me.shark0822.combatSystem.damage.type.DamageType;
import me.shark0822.combatSystem.damage.receiver.DamageReceiver;
import me.shark0822.combatSystem.damage.receiver.DamageReceiverRegistry;
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
        if (!(entity instanceof LivingEntity target)) return;
        if (DamageApplier.isProcessing(entity)) return;

        double baseDamage = event.getDamage();
        double vanillaDamage = VanillaDamageCalculator.calculateReducedDamage(target, baseDamage, event.getCause(), false, false, false);

        DamageType damageType = DamageType.NONE;
        AttackType attackType = getAttackType(event);
        DamageInstance instance = new DamageInstance(vanillaDamage, damageType, attackType);

        Entity damager = null;
        DamageReceiver attackerReceiver = null;

        if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            if (damageByEntityEvent.getDamager() instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Entity shooter) {
                    damager = shooter;
                    attackerReceiver = DamageReceiverRegistry.get(shooter.getUniqueId());
                }
            } else {
                damager = damageByEntityEvent.getDamager();
                attackerReceiver = DamageReceiverRegistry.get(damager.getUniqueId());
            }
        }

        DamageReceiver targetReceiver = DamageReceiverRegistry.get(target.getUniqueId());
        double finalDamage = StatDamageCalculator.calculateFinalDamage(vanillaDamage, instance, attackerReceiver, targetReceiver);

        CombatDamageEvent preEvent = new CombatDamageEvent(damager, target, baseDamage, vanillaDamage, finalDamage, attackerReceiver, targetReceiver, instance, event.getCause());
        Bukkit.getPluginManager().callEvent(preEvent);
        if (preEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(1e-6);
        HealthManager.applyDamage(target, damager, preEvent.getFinalDamage());
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
