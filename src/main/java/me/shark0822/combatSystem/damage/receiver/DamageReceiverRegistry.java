package me.shark0822.combatSystem.damage.receiver;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageReceiverRegistry {
    private static final Map<UUID, DamageReceiver> receiverCache = new HashMap<>();

    public static DamageReceiver get(LivingEntity entity) {
        return get(entity.getUniqueId());
    }

    public static DamageReceiver get(UUID uuid) {
        return receiverCache.computeIfAbsent(uuid, DamageReceiver::new);
    }

    public static void remove(UUID uuid) {
        receiverCache.remove(uuid);
    }

    public static void remove(LivingEntity entity) {
        receiverCache.remove(entity.getUniqueId());
    }

    public static void clear() {
        receiverCache.clear();
    }
}