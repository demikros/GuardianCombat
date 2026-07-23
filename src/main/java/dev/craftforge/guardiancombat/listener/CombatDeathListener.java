package dev.craftforge.guardiancombat.listener;

import dev.craftforge.guardiancombat.combat.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class CombatDeathListener implements Listener {

    private final CombatManager combatManager;

    public CombatDeathListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        combatManager.untag(victim.getUniqueId());
    }
}
