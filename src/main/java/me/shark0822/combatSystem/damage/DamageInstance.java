package me.shark0822.combatSystem.damage;

public class DamageInstance {

    private double baseDamage;
    private DamageType damageType;
    private AttackType attackType;

    public DamageInstance(double baseDamage, DamageType damageType, AttackType attackType) {
        this.baseDamage = baseDamage;
        this.attackType = attackType;
        this.damageType = damageType;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(double baseDamage) {
        this.baseDamage = baseDamage;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType type) {
        this.damageType = type;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public void setAttackType(AttackType attackType) {
        this.attackType = attackType;
    }
}
