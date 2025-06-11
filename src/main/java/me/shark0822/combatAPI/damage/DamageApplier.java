package me.shark0822.combatAPI.damage;

import me.shark0822.combatAPI.commands.DebugMode;
import me.shark0822.combatAPI.events.PreviousDamageEvent;
import me.shark0822.combatAPI.stats.DamageReceiver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DamageApplier {

    public static void applyDamage(Entity damager, Entity target, DamageInstance instance) {
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

            double finalDamage = DamageCalculator.calculateFinalDamage(instance, damagerReceiver, targetReceiver);

            PreviousDamageEvent preEvent = new PreviousDamageEvent(
                    damager,
                    target,
                    baseDamage,
                    finalDamage,
                    damagerReceiver,
                    targetReceiver,
                    instance
            );

            Bukkit.getPluginManager().callEvent(preEvent);
            if (DebugMode.isDebugMode()) Bukkit.getLogger().info("[CombatAPI][DamageApplier] PreviousDamageEvent 호출됨: " + preEvent);

            if (preEvent.isCancelled()) {
                if (DebugMode.isDebugMode()) Bukkit.getLogger().info("[CombatAPI][DamageApplier] 피해 적용 취소됨");
                return;
            }

            double damageToApply = preEvent.getFinalDamage();

            targetEntity.damage(damageToApply, damager);
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
