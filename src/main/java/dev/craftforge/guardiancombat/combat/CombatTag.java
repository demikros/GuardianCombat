package dev.craftforge.guardiancombat.combat;

import net.kyori.adventure.bossbar.BossBar;

import java.util.UUID;

public final class CombatTag {

    private final UUID opponentUuid;
    private final String opponentName;
    private long expiryEpochMillis;
    private final BossBar bossBar;

    public CombatTag(UUID opponentUuid, String opponentName, long expiryEpochMillis, BossBar bossBar) {
        this.opponentUuid = opponentUuid;
        this.opponentName = opponentName;
        this.expiryEpochMillis = expiryEpochMillis;
        this.bossBar = bossBar;
    }

    public UUID getOpponentUuid() {
        return opponentUuid;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public long getExpiryEpochMillis() {
        return expiryEpochMillis;
    }

    public void setExpiryEpochMillis(long expiryEpochMillis) {
        this.expiryEpochMillis = expiryEpochMillis;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public long getRemainingMillis() {
        return Math.max(0L, expiryEpochMillis - System.currentTimeMillis());
    }

    public int getRemainingSeconds() {
        return (int) Math.ceil(getRemainingMillis() / 1000.0);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expiryEpochMillis;
    }
}
