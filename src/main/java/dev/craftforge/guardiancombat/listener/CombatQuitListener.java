package dev.craftforge.guardiancombat.listener;

import dev.craftforge.guardiancombat.combat.CombatManager;
import dev.craftforge.guardiancombat.util.Messages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class CombatQuitListener implements Listener {

    private final CombatManager combatManager;
    private final Messages messages;
    private final FileConfiguration config;

    public CombatQuitListener(CombatManager combatManager, Messages messages, FileConfiguration config) {
        this.combatManager = combatManager;
        this.messages = messages;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!combatManager.isTagged(player.getUniqueId())) {
            return;
        }
        punishCombatLog(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        if (!config.getBoolean("punish-on-kick", true)) {
            return;
        }
        Player player = event.getPlayer();
        if (!combatManager.isTagged(player.getUniqueId())) {
            return;
        }
        punishCombatLog(player);
    }

    private void punishCombatLog(Player player) {
        String name = player.getName();
        combatManager.untag(player.getUniqueId());
        player.setHealth(0.0);
        messages.broadcastCombatLog(name);
    }
}
