package dev.craftforge.guardiancombat.listener;

import dev.craftforge.guardiancombat.combat.CombatManager;
import dev.craftforge.guardiancombat.util.Messages;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class CombatRestrictionListener implements Listener {

    private final CombatManager combatManager;
    private final Messages messages;
    private final FileConfiguration config;

    public CombatRestrictionListener(CombatManager combatManager, Messages messages, FileConfiguration config) {
        this.combatManager = combatManager;
        this.messages = messages;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("guardiancombat.bypass")) {
            return;
        }
        if (!combatManager.isTagged(player.getUniqueId())) {
            return;
        }

        String message = event.getMessage().toLowerCase();
        String rootCommand = message.split(" ")[0];

        List<String> blocked = config.getStringList("blocked-commands");
        for (String blockedEntry : blocked) {
            String normalized = blockedEntry.toLowerCase();
            if (rootCommand.equals(normalized) || rootCommand.startsWith(normalized + " ") || rootCommand.startsWith(normalized + ":")) {
                event.setCancelled(true);
                String displayCommand = rootCommand.startsWith("/") ? rootCommand.substring(1) : rootCommand;
                messages.sendCommandBlocked(player, displayCommand);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("guardiancombat.bypass")) {
            return;
        }
        if (!combatManager.isTagged(player.getUniqueId())) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        if (config.getBoolean("block-enderpearl", false) && item.getType() == Material.ENDER_PEARL) {
            event.setCancelled(true);
            messages.sendEnderpearlBlocked(player);
            return;
        }

        if (config.getBoolean("block-chorus", true) && item.getType() == Material.CHORUS_FRUIT) {
            event.setCancelled(true);
            messages.sendChorusBlocked(player);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!config.getBoolean("block-elytra", true)) {
            return;
        }
        Player player = event.getPlayer();
        if (player.hasPermission("guardiancombat.bypass")) {
            return;
        }
        if (!combatManager.isTagged(player.getUniqueId())) {
            return;
        }
        if (player.isGliding()) {
            player.setGliding(false);
            messages.sendElytraBlocked(player);
        }
    }
}
