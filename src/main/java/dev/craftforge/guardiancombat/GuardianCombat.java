package dev.craftforge.guardiancombat;

import dev.craftforge.guardiancombat.combat.CombatManager;
import dev.craftforge.guardiancombat.command.CombatCommand;
import dev.craftforge.guardiancombat.listener.CombatDeathListener;
import dev.craftforge.guardiancombat.listener.CombatListener;
import dev.craftforge.guardiancombat.listener.CombatQuitListener;
import dev.craftforge.guardiancombat.listener.CombatRestrictionListener;
import dev.craftforge.guardiancombat.util.Messages;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class GuardianCombat extends JavaPlugin {

    private CombatManager combatManager;
    private Messages messages;
    private BukkitTask tickTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        messages = new Messages(getConfig());
        combatManager = new CombatManager(messages, getConfig());

        registerListeners();
        registerCommands();

        tickTask = getServer().getScheduler().runTaskTimer(this, combatManager::tick, 5L, 5L);
    }

    @Override
    public void onDisable() {
        if (tickTask != null) {
            tickTask.cancel();
        }
        if (combatManager != null) {
            combatManager.clearAll();
        }
    }

    public void reloadPluginConfig() {
        reloadConfig();
        messages.reloadConfig(getConfig());
        combatManager.reloadConfig(getConfig());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new CombatListener(combatManager), this);
        pm.registerEvents(new CombatQuitListener(combatManager, messages, getConfig()), this);
        pm.registerEvents(new CombatRestrictionListener(combatManager, messages, getConfig()), this);
        pm.registerEvents(new CombatDeathListener(combatManager), this);
    }

    private void registerCommands() {
        CombatCommand combatCommand = new CombatCommand(this, combatManager, messages);
        var cmd = getCommand("combat");
        if (cmd != null) {
            cmd.setExecutor(combatCommand);
            cmd.setTabCompleter(combatCommand);
        }
    }
}
