package me.shark0822.combatSystem.damage.calculator;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class VanillaDamageCalculator {

    public static double calculateReducedDamage(LivingEntity entity, double baseDamage, EntityDamageEvent.DamageCause cause, boolean ignoreArmor, boolean ignoreEnchantments, boolean ignoreResistance) {
        if (baseDamage <= 0) return 0;

        double damage = baseDamage;

        if (!ignoreArmor) {
            damage = applyArmorReduction(entity, damage);
        }

        if (!ignoreEnchantments) {
            damage = applyProtectionReduction(entity, damage, cause);
        }

        if (!ignoreResistance) {
            damage = applyResistanceReduction(entity, damage);
        }

        return Math.max(damage, 0);
    }

    private static double applyArmorReduction(LivingEntity entity, double damage) {
        AttributeInstance armorAttr = entity.getAttribute(Attribute.ARMOR);
        AttributeInstance toughnessAttr = entity.getAttribute(Attribute.ARMOR_TOUGHNESS);

        if (armorAttr == null || toughnessAttr == null) return damage;

        double armor = armorAttr.getValue();
        double toughness = toughnessAttr.getValue();

        double reduction = Math.min(20.0, Math.max(armor / 5.0, armor - damage / (2.0 + toughness / 4.0))) / 25.0;

        return damage * (1.0 - reduction);
    }

    private static double applyProtectionReduction(LivingEntity entity, double damage, EntityDamageEvent.DamageCause cause) {
        int epf = 0;

        for (ItemStack armor : entity.getEquipment().getArmorContents()) {
            if (armor == null) continue;

            switch (cause) {
                case FIRE, FIRE_TICK, LAVA -> epf += armor.getEnchantmentLevel(Enchantment.FIRE_PROTECTION) * 1.25;
                case PROJECTILE -> epf += armor.getEnchantmentLevel(Enchantment.PROJECTILE_PROTECTION) * 1.5;
                case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> epf += armor.getEnchantmentLevel(Enchantment.BLAST_PROTECTION) * 1.5;
                case FALL -> epf += armor.getEnchantmentLevel(Enchantment.FEATHER_FALLING) * 2.0;
            }

            epf += armor.getEnchantmentLevel(Enchantment.PROTECTION);
        }

        epf = Math.min(epf, 20);
        double reduction = epf * 0.04;
        return damage * (1.0 - reduction);
    }

    private static double applyResistanceReduction(LivingEntity entity, double damage) {
        if (entity.hasPotionEffect(PotionEffectType.RESISTANCE)) {
            int amplifier = entity.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier();
            double reduction = (amplifier + 1) * 0.2;
            return damage * (1.0 - Math.min(reduction, 1.0));
        }
        return damage;
    }
}
