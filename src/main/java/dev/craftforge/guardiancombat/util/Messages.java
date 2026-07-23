package dev.craftforge.guardiancombat.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class Messages {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private FileConfiguration config;

    public Messages(FileConfiguration config) {
        this.config = config;
    }

    public void reloadConfig(FileConfiguration newConfig) {
        this.config = newConfig;
    }

    public void send(Audience audience, String key) {
        String raw = config.getString("messages." + key, "<red>Missing message: " + key);
        audience.sendMessage(MM.deserialize(raw));
    }

    public void sendTagged(Player player, String opponentName) {
        String raw = config.getString("messages.combat-tagged", "<red>You are now in combat!");
        String resolved = raw.replace("{opponent}", opponentName);
        player.sendMessage(MM.deserialize(resolved));
    }

    public void sendExpired(Player player) {
        send(player, "combat-expired");
    }

    public void broadcastCombatLog(String playerName) {
        String raw = config.getString("messages.combat-log-broadcast", "<dark_red>{player} combat-logged!");
        String resolved = raw.replace("{player}", playerName);
        Component message = MM.deserialize(resolved);
        Bukkit.getServer().sendMessage(message);
    }

    public void sendCommandBlocked(Player player, String command) {
        String raw = config.getString("messages.command-blocked", "<red>You cannot use /{command} in combat!");
        String resolved = raw.replace("{command}", command);
        player.sendMessage(MM.deserialize(resolved));
    }

    public void sendElytraBlocked(Player player) {
        send(player, "elytra-blocked");
    }

    public void sendEnderpearlBlocked(Player player) {
        send(player, "enderpearl-blocked");
    }

    public void sendChorusBlocked(Player player) {
        send(player, "chorus-blocked");
    }

    public void sendStatusSelfTagged(Player player, String opponentName, int seconds) {
        String raw = config.getString("messages.status-self-tagged", "<yellow>In combat with {opponent}. {seconds}s remaining.");
        String resolved = raw.replace("{opponent}", opponentName).replace("{seconds}", String.valueOf(seconds));
        player.sendMessage(MM.deserialize(resolved));
    }

    public void sendStatusSelfClear(Player player) {
        send(player, "status-self-clear");
    }

    public void sendStatusOtherTagged(Audience sender, String targetName, String opponentName, int seconds) {
        String raw = config.getString("messages.status-other-tagged", "<yellow>{player} is in combat with {opponent}. {seconds}s remaining.");
        String resolved = raw.replace("{player}", targetName).replace("{opponent}", opponentName).replace("{seconds}", String.valueOf(seconds));
        sender.sendMessage(MM.deserialize(resolved));
    }

    public void sendStatusOtherClear(Audience sender, String targetName) {
        String raw = config.getString("messages.status-other-clear", "<yellow>{player} is not in combat.");
        String resolved = raw.replace("{player}", targetName);
        sender.sendMessage(MM.deserialize(resolved));
    }

    public void sendPlayerNotFound(Audience sender, String name) {
        String raw = config.getString("messages.status-player-not-found", "<red>Player {player} not found.");
        String resolved = raw.replace("{player}", name);
        sender.sendMessage(MM.deserialize(resolved));
    }

    public void sendNoPermission(Audience sender) {
        send(sender, "no-permission");
    }

    public void sendConfigReloaded(Audience sender) {
        send(sender, "config-reloaded");
    }

    public void sendUsageCombat(Audience sender) {
        send(sender, "usage-combat");
    }
}
