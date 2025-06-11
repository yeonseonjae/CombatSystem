package me.shark0822.combatAPI.events;

import me.shark0822.combatAPI.damage.DamageInstance;
import me.shark0822.combatAPI.stats.DamageReceiver;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PreviousDamageEvent  extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Entity damager;
    private final Entity entity;
    private DamageReceiver damagerReceiver;
    private DamageReceiver entityReceiver;
    private DamageInstance damageInstance;
    private double baseDamage;
    private double finalDamage;
    private boolean cancelled;

    public PreviousDamageEvent(Entity damager, Entity entity, double baseDamage, double finalDamage, DamageReceiver damagerReceiver, DamageReceiver entityReceiver,
                               DamageInstance damageInstance) {
        this.damager = damager;
        this.entity = entity;
        this.baseDamage = baseDamage;
        this.finalDamage = finalDamage;
        this.damagerReceiver = damagerReceiver;
        this.entityReceiver = entityReceiver;
        this.damageInstance = damageInstance;
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
