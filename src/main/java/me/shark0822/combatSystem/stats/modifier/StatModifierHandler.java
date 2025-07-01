package me.shark0822.combatSystem.stats.modifier;

import me.shark0822.combatSystem.stats.StatType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatModifierHandler {
    private final List<StatModifier> modifiers = new ArrayList<>();

    public void tickAll() {
        Iterator<StatModifier> iter = modifiers.iterator();
        while (iter.hasNext()) {
            StatModifier mod = iter.next();
            mod.tick();
            if (mod.isExpired()) {
                mod.onExpire();
                iter.remove();
            }
        }
    }

    public void addModifier(StatModifier newMod) {
        newMod.onApply();
        modifiers.add(newMod);
    }

    public float getModifierTotal(StatType stat) {
        float total = 0f;
        for (StatModifier mod : modifiers) {
            if (mod.getStat() == stat) {
                total += mod.getEffectiveValue();
            }
        }
        return total;
    }

    public List<StatModifier> getActiveModifiers() {
        return new ArrayList<>(modifiers);
    }
}
