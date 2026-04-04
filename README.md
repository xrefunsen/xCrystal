# xCustomCrystal

![Paper](https://img.shields.io/badge/Paper-1.21-004080?style=flat)
![Java](https://img.shields.io/badge/Java-17+-e58916?style=flat)

**xCustomCrystal** is a **Paper 1.21** plugin by **[xrefunsen](https://github.com/xrefunsen)**. It adds **Echo Shard** “crystal” items you can **craft** or **give with commands**. Each crystal has a fixed **id**, styled **name/lore**, a **30s cooldown** per player per type, and a **right-click** power (buffs, AoE debuffs, lightning, teleport, silence, mirror reflect, mining burst, fireball, mob morph, and more).

**In-game language** comes from **`lang/tr.yml`** or **`lang/eu.yml`** (English UI strings), chosen with **`language`** in **`plugins/xCustomCrystal/config.yml`**.

**Random Crystal** is crafted as a **Nether Star** item: **right-click** consumes it and rolls **one** random crystal from all registered ids. Item data uses **`crystal_id`** on normal crystals and **`is_random_crystal`** on the random item (Paper **PersistentDataContainer**).

**Repository:** [github.com/xrefunsen/xCrystal](https://github.com/xrefunsen/xCrystal) · **Main class:** `xrefunsen.xcustomcrystal.XCustomCrystalPlugin`

---

**İçindekiler:** aşağıda tam İngilizce dokümantasyon; sayfa sonuna doğru **Türkçe** özet bölümü vardır.

---

## Requirements

- **Java 17+**
- **Paper** 1.21 (or a compatible fork)
- **Maven** 3.6+ to build from this repo

## Build

```bash
mvn -B package
```

Artifact: **`target/xCustomCrystal.jar`**

## Installation

1. Build with Maven or use a released jar.
2. Put **`xCustomCrystal.jar`** in the server **`plugins`** folder.
3. Start the server once to create **`plugins/xCustomCrystal/`**.
4. Set **`language`** to **`tr`** or **`eu`** in **`config.yml`** (`eu` = English messages).
5. Restart the server (or reload only if you know it is safe for your setup).

## Configuration

| Key | Values | Description |
| --- | --- | --- |
| `language` | `tr`, `eu` | Which YAML pack loads for chat and item text. |

Edit **`src/main/resources/lang/tr.yml`** / **`lang/eu.yml`** in source, then rebuild, to change names, lore, or message keys.

---

## Features (summary)

| Topic | Detail |
| --- | --- |
| Crystals | **29** types, each with unique right-click behaviour. |
| Cooldown | Per **player** + **crystal id** (default **30s** in definitions). |
| Silence | Blocks hotbar swap, break/place, drops, and some use actions on affected players. |
| Mirror | Reflects **50%** of damage back to the attacker for a short timer. |
| Moon | Buffs only during **night** (world time ~**13000–23000**). |
| Admin | **`/kristal`** gives any crystal or a Random Crystal (**permission** required). |

---

## Random Crystal crafting (shaped)

Internal recipe key: **`random_crystal`**. Shape in code: **`NEN`** / **`EWE`** / **`NEN`**.

```
[N] [E] [N]
[E] [W] [E]
[N] [E] [N]
```

| Symbol | Item |
| --- | --- |
| **N** | Netherite Ingot |
| **E** | Diamond Block |
| **W** | Nether Star (middle) |

**Why stacks look huge:** before the craft is allowed, every **E** cell needs **≥ 12** Diamond Blocks and every **N** cell **≥ 3** Netherite Ingots. After success, the plugin (next tick) removes **11** from each **E** stack and **2** from each **N** stack — real cost **44** Diamond Blocks, **8** Netherite Ingots, **1** Nether Star.

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

## Commands and permissions

| Command | Description |
| --- | --- |
| `/kristal` | Show usage (**permission** required). |
| `/kristal <crystal_id>` | Give that crystal (example: `guc_kristali`). |
| `/kristal random` | Give a **Random Crystal** (alias: `/kristal rastgele`). |

| Permission | Description | Default |
| --- | --- | --- |
| `xcustomcrystal.admin` | Use **`/kristal`** | `op` |

---

## Türkçe

**xCustomCrystal**, **xrefunsen**’ın **Paper 1.21** için geliştirdiği bir eklentidir. **Yankı Parçası** görünümlü **kristal** eşyaları ekler; bunlar **üretim masasında** yapılabilir veya **`/kristal`** ile verilebilir. Her kristalin sabit **id**’si, renkli **isim/lore**’u, oyuncu + kristal türü başına **30 saniye** bekleme süresi ve **sağ tık** ile çalışan özel bir gücü vardır (iksirler, alan etkileri, yıldırım, ışınlanma, sessizlik, ayna, madenci patlaması, ateş topu, canavar dönüşümü vb.).

**Oyun içi metinler** **`config.yml`** içindeki **`language`** ile **`tr`** veya **`eu`** (İngilizce arayüz metinleri) seçilir; dosyalar **`lang/tr.yml`** ve **`lang/eu.yml`**.

**Rastgele Kristal** ayrı bir **Nether Yıldızı** eşyasıdır; **sağ tık** ile tüketilir ve kayıtlı kristallerden **biri** rastgele verilir.

### Gereksinimler

Java **17+**, **Paper 1.21**, kaynak derlemek için **Maven 3.6+**.

### Derleme ve kurulum

```bash
mvn -B package
```

1. **`target/xCustomCrystal.jar`** dosyasını sunucunun **`plugins`** klasörüne atın.  
2. Sunucuyu bir kez çalıştırın, **`plugins/xCustomCrystal/config.yml`** içinde **`language`** değerini **`tr`** veya **`eu`** yapın.  
3. Sunucuyu yeniden başlatın.

### Rastgele Kristal (üretim özeti)

Aynı **3×3** şema: köşeler ve orta yanlar **N** = Netherite Külçe, kenar ortalar **E** = Elmas Blok, merkez **W** = Nether Yıldızı. Her **E** hücrede en az **12**, her **N** hücrede en az **3** eşya gerekir; başarılı üretimde gerçek maliyet yaklaşık **44 Elmas Blok**, **8 Netherite Külçe**, **1 Nether Yıldızı** (detay yukarıdaki İngilizce tabloda).

### Komutlar ve izinler

| Komut | Açıklama |
| --- | --- |
| `/kristal` | Kullanımı gösterir (izin gerekir). |
| `/kristal <kristal_id>` | Belirtilen kristali verir (örn. `guc_kristali`). |
| `/kristal random` veya `/kristal rastgele` | Rastgele Kristal verir. |

| İzin | Açıklama | Varsayılan |
| --- | --- | --- |
| `xcustomcrystal.admin` | `/kristal` kullanımı | `op` |

**Kristal id listesi** yukarıdaki İngilizce bölümdeki blok ile aynıdır.

---

## License

**English:** Proprietary — see [`LICENSE`](LICENSE). All rights reserved by xrefunsen; no open-source license is granted.

**Türkçe:** Özel lisans — ayrıntı için [`LICENSE`](LICENSE). Tüm hakları xrefunsen’a aittir; açık kaynak lisansı verilmez.
