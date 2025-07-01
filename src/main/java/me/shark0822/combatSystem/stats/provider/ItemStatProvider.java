package me.shark0822.combatSystem.stats.provider;

import me.shark0822.combatSystem.stats.StatType;
import me.shark0822.combatSystem.stats.StatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ItemStatProvider implements StatProvider {

    private final UUID uuid;

    public ItemStatProvider(UUID uuid) {
        this.uuid = uuid;
    }

    private LivingEntity getEntity() {
        Entity entity = Bukkit.getEntity(uuid);
        if (entity instanceof LivingEntity) {
            return (LivingEntity) entity;
        }
        return null;
    }

    @Override
    public double getBaseStat(StatType type) {
        double total = 0.0;

        // 착용 아이템 전체 탐색
        for (ItemStack item : getEquippedItems()) {
            if (item == null || item.getType() == Material.AIR) continue;
            var meta = item.getItemMeta();
            if (meta == null || !meta.getPersistentDataContainer().has(StatUtil.getKey(type), PersistentDataType.DOUBLE)) continue;

            Double value = meta.getPersistentDataContainer().get(StatUtil.getKey(type), PersistentDataType.DOUBLE);
            if (value != null) total += value;
        }

        return total;
    }

    @Override
    public double getTemporaryStat(StatType type) {
        // 장비 아이템에는 임시 스탯이 없다고 가정
        return 0.0;
    }

    private ItemStack[] getEquippedItems() {
        // 주 무기, 보조 무기, 갑옷 4부위 포함
        return new ItemStack[] {
                getEntity().getEquipment().getItemInMainHand(),
                getEntity().getEquipment().getItemInOffHand(),
                getEntity().getEquipment().getHelmet(),
                getEntity().getEquipment().getChestplate(),
                getEntity().getEquipment().getLeggings(),
                getEntity().getEquipment().getBoots()
        };
    }
}
