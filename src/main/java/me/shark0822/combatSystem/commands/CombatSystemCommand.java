package me.shark0822.combatSystem.commands;

import me.shark0822.combatSystem.damage.*;
import me.shark0822.combatSystem.damage.type.AttackType;
import me.shark0822.combatSystem.damage.type.DamageType;
import me.shark0822.combatSystem.stats.*;
import me.shark0822.combatSystem.damage.receiver.DamageReceiver;
import me.shark0822.combatSystem.damage.receiver.DamageReceiverRegistry;
import me.shark0822.combatSystem.stats.modifier.StatModifier;
import me.shark0822.combatSystem.stats.provider.DebugStatProvider;
import me.shark0822.combatSystem.stats.provider.StatProvider;
import me.shark0822.combatSystem.stats.provider.StatProviderManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class CombatSystemCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "사용법: /combatsystem <stat|debug|damage|modifier> ...");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "stat" -> handleStatCommand(sender, args);
            case "debug" -> handleDebugCommand(sender);
            case "damage" -> handleDamageCommand(sender, args);
            case "modifier" -> handleModifierCommand(sender, args);
            default -> sender.sendMessage(ChatColor.RED + "알 수 없는 하위 명령어입니다.");
        }

        return true;
    }

    private void handleStatCommand(CommandSender sender, String[] args) {
        if (args.length < 5) {
            sender.sendMessage(ChatColor.RED + "사용법: /combatsystem stat <엔티티> <스탯타입> <base|temp|final> <get|set|reset> [값]");
            return;
        }

        String targetName = args[1];
        String statName = args[2].toUpperCase();
        String statMode = args[3].toLowerCase();
        String action = args[4].toLowerCase();

        List<Entity> targets = selectEntities(sender, targetName);
        if (targets.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "대상 엔티티를 찾을 수 없습니다.");
            return;
        }

        StatType statType;
        try {
            statType = StatType.valueOf(statName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "유효하지 않은 스탯 타입입니다.");
            return;
        }

        for (Entity target : targets) {
            DamageReceiver receiver = new DamageReceiver(target.getUniqueId());

            boolean isBase = statMode.equals("base");
            boolean isTemp = statMode.equals("temp");
            boolean isFinal = statMode.equals("final");

            if (!isBase && !isTemp && !isFinal) {
                sender.sendMessage(ChatColor.RED + "세 번째 인자는 'base', 'temp', 'final' 중 하나여야 합니다.");
                return;
            }

            switch (action) {
                case "get" -> {
                    double value = isBase
                            ? receiver.getBaseStat(statType)
                            : isTemp
                            ? receiver.getStatModifierHandler().getModifierTotal(statType)
                            : receiver.getFinalStat(statType);

                    sender.sendMessage(ChatColor.GREEN + target.getName() + "의 " + statType.name() + " (" + statMode + ") 값은 " + value + "입니다.");
                }
                case "set" -> {
                    if (!isBase) {
                        sender.sendMessage(ChatColor.RED + "'set' 명령은 base 모드에서만 가능합니다.");
                        return;
                    }
                    if (!DebugMode.isDebugMode()) return;

                    if (args.length < 6) {
                        sender.sendMessage(ChatColor.RED + "값을 입력하세요.");
                        return;
                    }
                    double value;
                    try {
                        value = Double.parseDouble(args[5]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "값은 숫자여야 합니다.");
                        return;
                    }
                    receiver.setBaseStat(statType, value);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + "의 " + statType.name() + " 값이 " + value + "로 설정되었습니다.");
                }
                case "reset" -> {
                    if (!isBase) {
                        sender.sendMessage(ChatColor.RED + "'reset' 명령은 base 모드에서만 가능합니다.");
                        return;
                    }
                    if (!DebugMode.isDebugMode()) return;

                    receiver.setBaseStat(statType, 0.0);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + "의 " + statType.name() + " 값이 초기화되었습니다.");
                }
                default -> sender.sendMessage(ChatColor.RED + "알 수 없는 동작입니다. (get, set, reset 중 하나여야 합니다)");
            }
        }
    }

    private final Function<UUID, StatProvider> debugProviderFactory = DebugStatProvider::new;

    private void handleDebugCommand(CommandSender sender) {
        boolean current = DebugMode.isDebugMode();
        DebugMode.setDebugMode(!current);
        sender.sendMessage(ChatColor.GREEN + "디버그 모드가 " + (!current ? "활성화" : "비활성화") + "되었습니다.");

        if (!current) {
            StatProviderManager.registerProviderFactory(debugProviderFactory);
        } else {
            StatProviderManager.unregisterProviderFactory(debugProviderFactory);
        }
    }

    private void handleDamageCommand(CommandSender sender, String[] args) {
        if (args.length < 5) {
            sender.sendMessage(ChatColor.RED + "사용법: /combatsystem damage <대상> <DamageType> <AttackType> <대미지값> [<ignoreArmor>] [<ignoreEnchantments>] [<ignoreResistance>] [by <공격자>]");
            return;
        }

        String selector = args[1];
        List<Entity> targets = selectEntities(sender, selector);
        if (targets.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "대상 엔티티를 찾을 수 없습니다: " + selector);
            return;
        }

        DamageType damageType;
        try {
            damageType = DamageType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "유효하지 않은 대미지 타입입니다: " + args[2]);
            return;
        }

        AttackType attackType;
        try {
            attackType = AttackType.valueOf(args[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "유효하지 않은 공격 타입입니다: " + args[3]);
            return;
        }

        double damageValue;
        try {
            damageValue = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "대미지 값은 숫자여야 합니다: " + args[4]);
            return;
        }

        boolean ignoreArmor = false;
        boolean ignoreEnchantments = false;
        boolean ignoreResistance = false;
        Entity attacker = null;

        int i = 5;
        if (args.length > i) {
            String a = args[i].toLowerCase();
            if (!a.equals("true") && !a.equals("false")) {
                sender.sendMessage(ChatColor.RED + "ignoreArmor에는 true 또는 false만 사용할 수 있습니다. 입력값: " + a);
                return;
            }
            ignoreArmor = Boolean.parseBoolean(a);
            i++;
        }
        if (args.length > i) {
            String b = args[i].toLowerCase();
            if (!b.equals("true") && !b.equals("false")) {
                sender.sendMessage(ChatColor.RED + "ignoreEnchantments에는 true 또는 false만 사용할 수 있습니다. 입력값: " + b);
                return;
            }
            ignoreEnchantments = Boolean.parseBoolean(b);
            i++;
        }
        if (args.length > i) {
            String c = args[i].toLowerCase();
            if (!c.equals("true") && !c.equals("false")) {
                sender.sendMessage(ChatColor.RED + "ignoreResistance에는 true 또는 false만 사용할 수 있습니다. 입력값: " + c);
                return;
            }
            ignoreResistance = Boolean.parseBoolean(c);
            i++;
        }

        if (args.length > i + 1 && args[i].equalsIgnoreCase("by")) {
            List<Entity> selected = selectEntities(sender, args[i + 1]);
            if (selected.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "공격자를 찾을 수 없습니다: " + args[i + 1]);
                return;
            }
            if (selected.size() > 1) {
                sender.sendMessage(ChatColor.RED + "공격자는 하나의 엔티티만 선택해야 합니다. (" + selected.size() + "개 선택됨)");
                return;
            }
            attacker = selected.get(0);
        }

        for (Entity target : targets) {
            if (!(target instanceof LivingEntity)) {
                sender.sendMessage(ChatColor.YELLOW + target.getName() + "은(는) LivingEntity가 아니므로 피해를 적용할 수 없습니다.");
                continue;
            }

            DamageInstance instance = new DamageInstance(damageValue, damageType, attackType);
            DamageApplier.applyDamage(target, attacker, instance, ignoreArmor, ignoreEnchantments, ignoreResistance);

            sender.sendMessage(ChatColor.GREEN + target.getName() + "에게 " + damageValue + "의 " +
                    damageType.name() + " (" + attackType.name() + ") 피해를 입혔습니다.");
        }
    }

    private void handleModifierCommand(CommandSender sender, String[] args) {
        if (args.length != 5) {
            sender.sendMessage(ChatColor.RED + "사용법: /combatsystem modifier <플레이어> <StatType> <값> <지속시간틱>");
            return;
        }

        String selector = args[1];
        List<Entity> targets = selectEntities(sender, selector);
        if (targets.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "대상 엔티티를 찾을 수 없습니다: " + selector);
            return;
        }

        StatType stat;
        try {
            stat = StatType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "유효하지 않은 스탯 타입입니다: " + args[2]);
            return;
        }

        float value;
        long duration;
        try {
            value = Float.parseFloat(args[3]);
            duration = Long.parseLong(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "값 또는 지속시간은 숫자여야 합니다.");
            return;
        }

        for (Entity target : targets) {
            StatModifier modifier = new StatModifier("manual_modifier_" + stat.name(), stat, value, duration, false);
            DamageReceiver receiver = DamageReceiverRegistry.get(target.getUniqueId());
            receiver.getStatModifierHandler().addModifier(modifier);

            sender.sendMessage(ChatColor.GREEN + target.getName() + "에게 " + stat.name() + " + " + value + " (" + duration + "틱) 버프를 적용했습니다.");
        }
    }

    private List<Entity> selectEntities(CommandSender sender, String selector) {
        if (selector.startsWith("@")) {
            return Bukkit.selectEntities(sender, selector);
        }

        List<Entity> list = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (entity.getName().equalsIgnoreCase(selector)) {
                    list.add(entity);
                }
            }
        }

        Player p = Bukkit.getPlayerExact(selector);
        if (p != null) list = Collections.singletonList(p);
        return list;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("stat".startsWith(args[0].toLowerCase())) completions.add("stat");
            if ("debug".startsWith(args[0].toLowerCase())) completions.add("debug");
            if ("damage".startsWith(args[0].toLowerCase())) completions.add("damage");
            if ("modifier".startsWith(args[0].toLowerCase())) completions.add("modifier");
            return completions;
        }

        if (args[0].equalsIgnoreCase("stat")) {
            if (args.length == 2) {
                completions.addAll(List.of("@e", "@a", "@p", "@s", "@r", "@n"));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String name = player.getName();
                    if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(name);
                    }
                }
                return completions;
            }

            if (args.length == 3) {
                for (StatType type : StatType.values()) {
                    String name = type.name().toLowerCase();
                    if (name.startsWith(args[2].toLowerCase())) {
                        completions.add(name);
                    }
                }
                return completions;
            }

            if (args.length == 4) {
                if ("base".startsWith(args[3].toLowerCase())) completions.add("base");
                if ("temp".startsWith(args[3].toLowerCase())) completions.add("temp");
                if ("final".startsWith(args[3].toLowerCase())) completions.add("final");
                return completions;
            }

            if (args.length == 5) {
                if ("get".startsWith(args[4].toLowerCase())) completions.add("get");

                if (DebugMode.isDebugMode() && args[3].equalsIgnoreCase("base")) {
                    if ("set".startsWith(args[4].toLowerCase())) completions.add("set");
                    if ("reset".startsWith(args[4].toLowerCase())) completions.add("reset");
                }
                return completions;
            }

            if (args.length == 6 && args[4].equalsIgnoreCase("set")) {
                completions.add("<value>");
                return completions;
            }
        }

        if (args[0].equalsIgnoreCase("damage")) {
            if (args.length == 2) {
                completions.addAll(List.of("@e", "@a", "@p", "@s", "@r", "@n"));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) completions.add(p.getName());
                }
                return completions;
            }

            if (args.length == 3) {
                for (DamageType dt : DamageType.values()) {
                    if (dt.name().toLowerCase().startsWith(args[2].toLowerCase())) completions.add(dt.name().toLowerCase());
                }
                return completions;
            }

            if (args.length == 4) {
                for (AttackType at : AttackType.values()) {
                    if (at.name().toLowerCase().startsWith(args[3].toLowerCase())) completions.add(at.name().toLowerCase());
                }
                return completions;
            }

            if (args.length == 5) {
                completions.add("<value>");
                return completions;
            }

            if (args.length >= 6 && args.length <= 8) {
                completions.add("true");
                completions.add("false");
                return completions;
            }

            if (args.length == 9) {
                completions.add("by");
                return completions;
            }

            if (args.length == 10 && args[8].equalsIgnoreCase("by")) {
                completions.addAll(List.of("@e", "@a", "@p", "@s", "@r", "@n"));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[9].toLowerCase())) completions.add(p.getName());
                }
                return completions;
            }
        }

        if (args[0].equalsIgnoreCase("modifier")) {
            if (args.length == 2) {
                completions.addAll(List.of("@e", "@a", "@p", "@s", "@r", "@n"));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) completions.add(p.getName());
                }
                return completions;
            }

            if (args.length == 3) {
                for (StatType type : StatType.values()) {
                    if (type.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                        completions.add(type.name().toLowerCase());
                    }
                }
                return completions;
            }

            if (args.length == 4) {
                completions.add("<value>");
                return completions;
            }

            if (args.length == 5) {
                completions.add("<ticks>");
                return completions;
            }
        }

        return completions;
    }
}
