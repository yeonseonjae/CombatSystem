package me.shark0822.combatSystem.damage;

import me.shark0822.combatSystem.commands.DebugMode;
import me.shark0822.combatSystem.damage.calculator.StatDamageCalculator;
import me.shark0822.combatSystem.damage.calculator.VanillaDamageCalculator;
import me.shark0822.combatSystem.events.CombatDamageEvent;
import me.shark0822.combatSystem.damage.receiver.DamageReceiver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DamageApplier {

    public static void applyDamage(Entity target, Entity damager, DamageInstance instance, boolean ignoreArmor, boolean ignoreEnchantments, boolean ignoreResistance) {
        if (!(target instanceof LivingEntity targetEntity) || instance == null) return;

        if (isProcessing(target)) {
            Bukkit.getLogger().info("[CombatAPI][DamageApplier] 이미 피해 처리 중인 엔티티: " + target.getName());
            return;
        }

        markProcessing(target);

        try {
            DamageReceiver damagerReceiver = (damager != null) ? new DamageReceiver(damager.getUniqueId()) : null;
            DamageReceiver targetReceiver = new DamageReceiver(target.getUniqueId());

            double baseDamage = instance.getBaseDamage();
            double vanillaDamage = VanillaDamageCalculator.calculateReducedDamage(targetEntity, baseDamage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, ignoreArmor, ignoreEnchantments, ignoreResistance);
            double finalDamage = StatDamageCalculator.calculateFinalDamage(vanillaDamage, instance, damagerReceiver, targetReceiver);

            CombatDamageEvent combatDamageEvent = new CombatDamageEvent(damager, target, baseDamage, vanillaDamage, finalDamage, damagerReceiver, targetReceiver, instance, EntityDamageEvent.DamageCause.ENTITY_ATTACK);

            Bukkit.getPluginManager().callEvent(combatDamageEvent);
            if (DebugMode.isDebugMode()) Bukkit.getLogger().info("[CombatAPI][DamageApplier] PreviousDamageEvent 호출됨: " + combatDamageEvent);

            if (combatDamageEvent.isCancelled()) {
                if (DebugMode.isDebugMode()) Bukkit.getLogger().info("[CombatAPI][DamageApplier] 피해 적용 취소됨");
                return;
            }

            double damageToApply = combatDamageEvent.getFinalDamage();

            HealthManager.applyDamage(targetEntity, damager, damageToApply);

            if (DebugMode.isDebugMode()) Bukkit.getLogger().info("[CombatAPI][DamageApplier] 실제 피해 적용: " + damageToApply + " 피해량, 대상: " + target.getName());
        } finally {
            unmarkProcessing(target);
            if (DebugMode.isDebugMode()) Bukkit.getLogger().info("[CombatAPI][DamageApplier] 피해 처리 완료: " + target.getName());
        }
    }

    private static final Set<UUID> processingEntities = new HashSet<>();

    public static boolean isProcessing(Entity entity) {
        return processingEntities.contains(entity.getUniqueId());
    }

    public static void markProcessing(Entity entity) {
        processingEntities.add(entity.getUniqueId());
    }

    public static void unmarkProcessing(Entity entity) {
        processingEntities.remove(entity.getUniqueId());
    }
}
