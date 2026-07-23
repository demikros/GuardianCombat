package dev.craftforge.guardiancombat.combat;

import dev.craftforge.guardiancombat.util.Messages;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CombatManager {

    private final ConcurrentHashMap<UUID, CombatTag> activeTags = new ConcurrentHashMap<>();
    private final Messages messages;
    private FileConfiguration config;

    public CombatManager(Messages messages, FileConfiguration config) {
        this.messages = messages;
        this.config = config;
    }

    public void reloadConfig(FileConfiguration newConfig) {
        this.config = newConfig;
    }

    public void tag(Player tagged, Player opponent) {
        final long durationMillis = config.getLong("combat-duration-seconds", 15L) * 1000L;
        final long newExpiry = System.currentTimeMillis() + durationMillis;
        final UUID taggedUuid = tagged.getUniqueId();

        CombatTag existing = activeTags.get(taggedUuid);
        if (existing != null) {
            existing.setExpiryEpochMillis(newExpiry);
            updateBossBar(tagged, existing, durationMillis);
            return;
        }

        BossBar bossBar = buildBossBar(durationMillis, durationMillis, opponent.getName());
        CombatTag tag = new CombatTag(opponent.getUniqueId(), opponent.getName(), newExpiry, bossBar);
        activeTags.put(taggedUuid, tag);

        if (config.getBoolean("bossbar.enabled", true)) {
            tagged.showBossBar(bossBar);
        }

        messages.sendTagged(tagged, opponent.getName());
    }

    public void tick() {
        for (Map.Entry<UUID, CombatTag> entry : activeTags.entrySet()) {
            UUID uuid = entry.getKey();
            CombatTag tag = entry.getValue();

            if (tag.isExpired()) {
                Player player = Bukkit.getPlayer(uuid);
                activeTags.remove(uuid);
                if (player != null) {
                    player.hideBossBar(tag.getBossBar());
                    messages.sendExpired(player);
                }
                continue;
            }

            Player player = Bukkit.getPlayer(uuid);
            if (player != null && config.getBoolean("bossbar.enabled", true)) {
                long totalMillis = config.getLong("combat-duration-seconds", 15L) * 1000L;
                updateBossBar(player, tag, totalMillis);
            }
        }
    }

    public boolean isTagged(UUID uuid) {
        CombatTag tag = activeTags.get(uuid);
        return tag != null && !tag.isExpired();
    }

    public CombatTag getTag(UUID uuid) {
        return activeTags.get(uuid);
    }

    public void untag(UUID uuid) {
        CombatTag tag = activeTags.remove(uuid);
        if (tag == null) {
            return;
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.hideBossBar(tag.getBossBar());
        }
    }

    public int getRemainingSeconds(UUID uuid) {
        CombatTag tag = activeTags.get(uuid);
        if (tag == null) {
            return 0;
        }
        return tag.getRemainingSeconds();
    }

    public void clearAll() {
        for (Map.Entry<UUID, CombatTag> entry : activeTags.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                player.hideBossBar(entry.getValue().getBossBar());
            }
        }
        activeTags.clear();
    }

    public Set<UUID> getTaggedUuids() {
        return activeTags.keySet();
    }

    private void updateBossBar(Player player, CombatTag tag, long totalMillis) {
        long remaining = tag.getRemainingMillis();
        float progress = totalMillis > 0 ? (float) remaining / totalMillis : 0f;
        progress = Math.max(0f, Math.min(1f, progress));

        String titleTemplate = config.getString("bossbar.title", "<red><bold>COMBAT</bold></red> <gray>|</gray> <yellow>{seconds}s</yellow>");
        String resolved = titleTemplate.replace("{seconds}", String.valueOf(tag.getRemainingSeconds()));
        Component title = MiniMessage.miniMessage().deserialize(resolved);

        tag.getBossBar().name(title);
        tag.getBossBar().progress(progress);
    }

    private BossBar buildBossBar(long remainingMillis, long totalMillis, String opponentName) {
        float progress = totalMillis > 0 ? (float) remainingMillis / totalMillis : 1f;

        String colorName = config.getString("bossbar.color", "RED").toUpperCase();
        BossBar.Color color;
        try {
            color = BossBar.Color.valueOf(colorName);
        } catch (IllegalArgumentException ignored) {
            color = BossBar.Color.RED;
        }

        String overlayName = config.getString("bossbar.overlay", "PROGRESS").toUpperCase();
        BossBar.Overlay overlay;
        try {
            overlay = BossBar.Overlay.valueOf(overlayName);
        } catch (IllegalArgumentException ignored) {
            overlay = BossBar.Overlay.PROGRESS;
        }

        int seconds = (int) Math.ceil(remainingMillis / 1000.0);
        String titleTemplate = config.getString("bossbar.title", "<red><bold>COMBAT</bold></red> <gray>|</gray> <yellow>{seconds}s</yellow>");
        String resolved = titleTemplate.replace("{seconds}", String.valueOf(seconds));
        Component title = MiniMessage.miniMessage().deserialize(resolved);

        return BossBar.bossBar(title, progress, color, overlay);
    }
}
