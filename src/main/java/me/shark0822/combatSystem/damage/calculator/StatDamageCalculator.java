package me.shark0822.combatSystem.damage.calculator;

import me.shark0822.combatSystem.commands.DebugMode;
import me.shark0822.combatSystem.damage.DamageInstance;
import me.shark0822.combatSystem.damage.type.DamageType;
import me.shark0822.combatSystem.damage.receiver.DamageReceiver;
import org.bukkit.Bukkit;

public class StatDamageCalculator {

    public static double calculateFinalDamage(double baseDamage, DamageInstance instance, DamageReceiver attacker, DamageReceiver defender) {
        DamageType type = instance.getDamageType();

        //피해 증가 (공격자)
        double damageMultiplier = 1.0;

        if (attacker != null) {
            damageMultiplier *= attacker.getDamageMultiplier(type);
        }

        //받는 피해 증가 (피격자)
        damageMultiplier *= defender.getTakenDamageMultiplier(type);

        //피해 감소 (피격자)
        double reduction = defender.getTotalDamageReduction();

        //피해 감소 무시 (공격자)
        double ignored = (attacker != null) ? attacker.getTotalReductionIgnored() : 0.0;

        double reductionAfterIgnore = Math.max(0.0, reduction - ignored);
        double finalMultiplier = damageMultiplier * (1.0 - reductionAfterIgnore);

        //최종 피해 계산
        double finalDamage = baseDamage * finalMultiplier;
        double result = Math.max(1e-6, finalDamage);

        if (DebugMode.isDebugMode()) {
            Bukkit.getLogger().info("[CombatAPI][DamageCalculator] 피해 계산 로그:");
            Bukkit.getLogger().info(" - 피격자: " + defender.getEntity().getName());
            Bukkit.getLogger().info(" - 원본 피해: " + baseDamage);
            Bukkit.getLogger().info(" - 피해 타입: " + type);
            Bukkit.getLogger().info(" - 공격 타입: " + instance.getAttackType());
            if (attacker != null) {
                double generalMultiplier = attacker.getDamageIncreaseGeneral();
                double elementalMultiplier = attacker.getDamageIncreaseElemental(type);
                Bukkit.getLogger().info(" - 공격자: " + attacker.getEntity().getName());
                Bukkit.getLogger().info(" - 공격자 일반 증폭 계수: " + generalMultiplier + " | " + generalMultiplier * 100 + "%");
                Bukkit.getLogger().info(" - 공격자 속성 증폭 계수: " + elementalMultiplier + " | " + elementalMultiplier * 100 + "%");
                Bukkit.getLogger().info(" - 무시된 피해 감소율: " + ignored + " | " + ignored * 100 + "%");
            }
            double generalMultiplier = defender.getTakenDamageIncreaseGeneral();
            double elementalMultiplier = defender.getTakenDamageIncreaseElemental(type);
            Bukkit.getLogger().info(" - 피격자 일반 증폭 계수: " + generalMultiplier + " | " + generalMultiplier * 100 + "%");
            Bukkit.getLogger().info(" - 피격자 속성 증폭 계수: " + elementalMultiplier + " | " + elementalMultiplier * 100 + "%");
            Bukkit.getLogger().info(" - 피격자 피해 감소율: " + reduction + " | " + reduction * 100 + "%");
            Bukkit.getLogger().info(" - 최종 계수: " + finalMultiplier + " | " + finalMultiplier * 100 + "%");
            Bukkit.getLogger().info(" - 최종 피해량: " + result);
        }

        return result;
    }
}