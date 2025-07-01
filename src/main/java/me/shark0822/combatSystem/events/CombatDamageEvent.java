package me.shark0822.combatSystem.events;

import me.shark0822.combatSystem.damage.DamageInstance;
import me.shark0822.combatSystem.damage.receiver.DamageReceiver;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatDamageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Entity damager;
    private final Entity entity;
    private DamageReceiver damagerReceiver;
    private DamageReceiver entityReceiver;
    private DamageInstance damageInstance;
    private double baseDamage;
    private double vanillaDamage;
    private double finalDamage;
    private boolean cancelled;
    private EntityDamageEvent.DamageCause cause;

    public CombatDamageEvent(Entity damager, Entity entity, double baseDamage, double vanillaDamage, double finalDamage,
                             DamageReceiver damagerReceiver, DamageReceiver entityReceiver,
                             DamageInstance damageInstance, EntityDamageEvent.DamageCause cause) {
        this.damager = damager;
        this.entity = entity;
        this.baseDamage = baseDamage;
        this.vanillaDamage = vanillaDamage;
        this.finalDamage = finalDamage;
        this.damagerReceiver = damagerReceiver;
        this.entityReceiver = entityReceiver;
        this.damageInstance = damageInstance;
        this.cause = cause;
    }

    public Entity getDamager() {
        return damager;
    }

    public Entity getEntity() {
        return entity;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(double baseDamage) {
        this.baseDamage = baseDamage;
    }

    public double getVanillaDamage() {
        return vanillaDamage;
    }

    public void setVanillaDamage(double vanillaDamage) {
        this.vanillaDamage = vanillaDamage;
    }

    public double getFinalDamage() {
        return finalDamage;
    }

    public void setFinalDamage(double finalDamage) {
        this.finalDamage = finalDamage;
    }

    public DamageReceiver getDamagerReceiver() {
        return damagerReceiver;
    }

    public void setDamagerReceiver(DamageReceiver damagerReceiver) {
        this.damagerReceiver = damagerReceiver;
    }

    public DamageReceiver getEntityReceiver() {
        return entityReceiver;
    }

    public void setEntityReceiver(DamageReceiver entityReceiver) {
        this.entityReceiver = entityReceiver;
    }

    public DamageInstance getDamageInstance() {
        return damageInstance;
    }

    public void setDamageInstance(DamageInstance damageInstance) {
        this.damageInstance = damageInstance;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }

    public void setCause(EntityDamageEvent.DamageCause cause) {
        this.cause = cause;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
