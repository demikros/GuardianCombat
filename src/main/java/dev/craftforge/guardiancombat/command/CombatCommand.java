package dev.craftforge.guardiancombat.command;

import dev.craftforge.guardiancombat.GuardianCombat;
import dev.craftforge.guardiancombat.combat.CombatManager;
import dev.craftforge.guardiancombat.combat.CombatTag;
import dev.craftforge.guardiancombat.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class CombatCommand implements CommandExecutor, TabCompleter {

    private final GuardianCombat plugin;
    private final CombatManager combatManager;
    private final Messages messages;

    public CombatCommand(GuardianCombat plugin, CombatManager combatManager, Messages messages) {
        this.plugin = plugin;
        this.combatManager = combatManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messages.sendUsageCombat(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "status" -> handleStatus(sender, args);
            case "reload" -> handleReload(sender);
            default -> messages.sendUsageCombat(sender);
        }

        return true;
    }

    private void handleStatus(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            if (!sender.hasPermission("guardiancombat.status.others")) {
                messages.sendNoPermission(sender);
                return;
            }
            String targetName = args[1];
            Player target = Bukkit.getPlayerExact(targetName);
            if (target == null) {
                messages.sendPlayerNotFound(sender, targetName);
                return;
            }
            CombatTag tag = combatManager.getTag(target.getUniqueId());
            if (tag != null && !tag.isExpired()) {
                messages.sendStatusOtherTagged(sender, target.getName(), tag.getOpponentName(), tag.getRemainingSeconds());
            } else {
                messages.sendStatusOtherClear(sender, target.getName());
            }
            return;
        }

        if (!(sender instanceof Player player)) {
            messages.sendUsageCombat(sender);
            return;
        }

        CombatTag tag = combatManager.getTag(player.getUniqueId());
        if (tag != null && !tag.isExpired()) {
            messages.sendStatusSelfTagged(player, tag.getOpponentName(), tag.getRemainingSeconds());
        } else {
            messages.sendStatusSelfClear(player);
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("guardiancombat.admin")) {
            messages.sendNoPermission(sender);
            return;
        }
        plugin.reloadPluginConfig();
        messages.sendConfigReloaded(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            if ("status".startsWith(partial)) {
                completions.add("status");
            }
            if (sender.hasPermission("guardiancombat.admin") && "reload".startsWith(partial)) {
                completions.add("reload");
            }
            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("status") && sender.hasPermission("guardiancombat.status.others")) {
            String partial = args[1].toLowerCase();
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getName().toLowerCase().startsWith(partial)) {
                    completions.add(online.getName());
                }
            }
            return completions;
        }

        return completions;
    }
}
