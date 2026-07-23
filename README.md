# ⚔️ GuardianCombat

> PvP combat tagging and anti-combat-log enforcement with a live boss-bar timer.

![Paper](https://img.shields.io/badge/Paper-1.21%2B-2196F3?style=for-the-badge&logo=minecraft&logoColor=white) ![Java](https://img.shields.io/badge/Java-21-E76F00?style=for-the-badge&logo=openjdk&logoColor=white) ![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white) ![License](https://img.shields.io/badge/License-MIT-3DA639?style=for-the-badge)

GuardianCombat tags players who engage in PvP and shows a live Adventure boss bar counting down their combat timer. Log out while tagged and you are killed on the spot, so combat-logging never pays off.

## ✨ Features

- Tags both attacker and victim on PvP damage
- Accurate attacker resolution for melee, projectiles (arrows/tridents) and tamed pets
- Live **Adventure boss bar** with configurable colour, overlay and title
- Kills players who quit (and optionally are kicked) while tagged — drops happen naturally
- Blocks configured commands, ender pearls / chorus fruit and elytra while in combat
- `guardiancombat.bypass` permission and fully configurable behaviour

## ⌨️ Commands

| Command | Aliases | Description | Permission |
| --- | --- | --- | --- |
| /combat status [player] | - | Show remaining combat time | `guardiancombat.use` |
| /combat reload | - | Reload the configuration | `guardiancombat.admin` |

## 🔐 Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `guardiancombat.use` | Use the /combat command | `true` |
| `guardiancombat.status.others` | Check other players' combat status | `op` |
| `guardiancombat.admin` | Reload and administrative features | `op` |
| `guardiancombat.bypass` | Bypass all combat restrictions | `false` |

## ⚙️ Configuration

Everything is configurable in `config.yml`:

- `combat-duration-seconds`
- `bossbar` block (enabled, colour, overlay, title template)
- `blocked-commands` list, `block-elytra`, `block-enderpearl`, `punish-on-kick`
- a full `messages:` section

## 📦 Installation

1. Download the latest release jar, or build it yourself (see below).
2. No hard dependencies.
3. Drop the jar into your server's `plugins/` folder and restart.

## 🛠️ Building from source

Requires **JDK 21** and **Maven 3.9+**.

```bash
mvn clean package
```

The runnable jar is written to `target/GuardianCombat-1.0.0.jar`.

## 🧱 Architecture

Packages: `combat` (manager + tag model), `listener` (damage, quit/kick, restrictions, death), `command`, `util`. A lightweight repeating task ticks tags and updates every boss bar.

This project targets **Paper 1.21+** and Paper forks (Purpur, Pufferfish). All user-facing text uses the Adventure API (MiniMessage), which is native to Paper. The source is written to a senior standard with clear package separation and no code comments.

## 📄 License

Released under the MIT License. © 2026 CraftForge.
