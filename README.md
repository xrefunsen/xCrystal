# xCustomCrystal

**xCustomCrystal** is a Paper **1.21** plugin by **xrefunsen** that adds craftable and command-givable **Echo Shard** items (“crystals”). Each crystal has a stable id, colored name and lore, a **30 second** cooldown per player per crystal type, and a **right-click** ability: self buffs, area debuffs, lightning, short teleport, silence aura, mirror damage reflection, miner blast, fireball, mob morph, and more. Messages and item text come from **`lang/tr.yml`** (Turkish) or **`lang/eu.yml`** (English), selected with **`language`** in **`config.yml`**.

**Random Crystal** is a separate **Nether Star** item: right-click consumes it and grants one random normal crystal. Crystals store their id in **PersistentDataContainer** (`crystal_id`); the random item uses `is_random_crystal`.

---

## Requirements

- Java **17+**
- **Paper** 1.21 (or a compatible fork)
- **Maven** 3.6+ if you build from source

## Build

```bash
mvn -B package
```

Output: **`target/xCustomCrystal.jar`**

## Installation

1. Run `mvn -B package` or use a prebuilt jar.
2. Copy **`xCustomCrystal.jar`** into the server **`plugins`** folder.
3. Start the server once.
4. Edit **`plugins/xCustomCrystal/config.yml`** and set **`language`** to **`tr`** or **`eu`**.
5. Restart the server (or reload plugins if your setup supports it safely).

## Configuration

| Key | Values | Description |
| --- | --- | --- |
| `language` | `tr`, `eu` | Chat and item strings (`eu` = English). |

To change crystal names, lore, or message keys, edit **`src/main/resources/lang/tr.yml`** and **`lang/eu.yml`** in the source tree, then rebuild the jar.

---

## Features (summary)

- **29** crystal types with distinct effects (potions, particles, sounds, area targeting).
- **Per-crystal cooldown** tracked per player.
- **Silence crystal** temporarily blocks other players’ item swap, block break/place, drop, and some interactions.
- **Mirror crystal** reflects part of incoming damage for a short window.
- **Moon crystal** only applies its buffs at **night** (world time roughly 13000–23000).
- **Admin command** `/kristal` to give crystals or a Random Crystal (permission-gated).

---

## Random Crystal crafting (shaped)

Recipe id in code: **`random_crystal`**. Grid is **3×3**; letters below match the code shape string (**`NEN` / `EWE` / `NEN`**).

```
[N] [E] [N]
[E] [W] [E]
[N] [E] [N]
```

| Letter | Material |
| --- | --- |
| **N** | Netherite Ingot |
| **E** | Diamond Block |
| **W** | Nether Star (center) |

**Stack rules (enforced by the plugin):** before the craft succeeds, every **E** slot must hold **at least 12** Diamond Blocks (per slot), and every **N** slot **at least 3** Netherite Ingots. After a successful craft, the plugin removes **11** from each **E** stack and **2** from each **N** stack (one tick later), so the real cost per craft is **44 Diamond Blocks** and **8 Netherite Ingots** plus **1 Nether Star**.

Right-click the crafted **Random Crystal** to turn it into one random normal crystal (uniform pick among registered ids).

---

## Crystal ids (for `/kristal <id>`)

```
ates_kristali          buz_kristali           simsek_kristali
yasam_kristali         karanlik_kristali      guc_kristali
hiz_kristali           zirh_kristali          zehir_kristali
isik_kristali          ruh_kristali           zaman_kristali
doga_kristali          su_kristali            toprak_kristali
hava_kristali          kan_kristali           olum_kristali
isinlanma_kristali     kaos_kristali          madenci_kristali
buyucu_kristali        yercekimi_kristali     ayna_kristali
hazine_kristali        metamorfoz_kristali    sessizlik_kristali
gunes_kristali         ay_kristali
```

---

## Commands and permissions (English)

| Command | Description |
| --- | --- |
| `/kristal` | Prints usage (needs permission). |
| `/kristal <crystal_id>` | Gives that crystal (e.g. `guc_kristali`). |
| `/kristal random` or `/kristal rastgele` | Gives a Random Crystal. |

| Permission | Description | Default |
| --- | --- | --- |
| `xcustomcrystal.admin` | Use `/kristal` | `op` |

---

## Komutlar ve izinler (Türkçe)

| Komut | Açıklama |
| --- | --- |
| `/kristal` | Kullanımı gösterir (izin gerekir). |
| `/kristal <kristal_id>` | İlgili kristali verir (örn. `guc_kristali`). |
| `/kristal random` veya `/kristal rastgele` | Rastgele Kristal verir. |

| İzin | Açıklama | Varsayılan |
| --- | --- | --- |
| `xcustomcrystal.admin` | `/kristal` kullanımı | `op` |

---

## License

**Proprietary / özel lisans** — see [`LICENSE`](LICENSE). All rights reserved by xrefunsen; no open-source license is granted.
