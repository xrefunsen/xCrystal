package xrefunsen.xcrystal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class CrystalListener implements Listener {

    private static final int SHORT_TICKS = 200;
    private static final int MEDIUM_TICKS = 300;
    private static final int LONG_TICKS = 400;

    private final XCrystalPlugin plugin;
    private final Map<String, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> mirrorCooldowns = new HashMap<>();
    private final Map<UUID, Long> silenceCooldowns = new HashMap<>();
    private final Random random = new Random();
    private final List<PotionEffectType> goodEffects;
    private final List<PotionEffectType> badEffects;
    private final List<EntityType> passiveMobs;
    private final Set<Material> minerBlocks;

    public CrystalListener(XCrystalPlugin plugin) {
        this.plugin = plugin;
        this.goodEffects = Arrays.asList(
                PotionEffectType.SPEED, PotionEffectType.STRENGTH, PotionEffectType.REGENERATION,
                PotionEffectType.JUMP_BOOST, PotionEffectType.RESISTANCE, PotionEffectType.FIRE_RESISTANCE,
                PotionEffectType.WATER_BREATHING, PotionEffectType.INVISIBILITY, PotionEffectType.NIGHT_VISION
        );
        this.badEffects = Arrays.asList(
                PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS, PotionEffectType.POISON,
                PotionEffectType.WITHER, PotionEffectType.BLINDNESS, PotionEffectType.NAUSEA
        );
        this.passiveMobs = Arrays.asList(
                EntityType.SHEEP, EntityType.PIG, EntityType.COW, EntityType.CHICKEN,
                EntityType.RABBIT, EntityType.MOOSHROOM
        );
        this.minerBlocks = Set.of(
                Material.STONE, Material.COBBLESTONE, Material.DIRT,
                Material.GRAVEL, Material.ANDESITE, Material.DIORITE,
                Material.GRANITE, Material.DEEPSLATE
        );
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Lang lang = plugin.getLang();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        NamespacedKey crystalKey = new NamespacedKey(plugin, "crystal_id");
        if (silenceCooldowns.containsKey(player.getUniqueId()) && silenceCooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
            ItemStack hand = event.getItem();
            if (hand == null || !hand.hasItemMeta()
                    || !hand.getItemMeta().getPersistentDataContainer().has(crystalKey, PersistentDataType.STRING)) {
                player.sendMessage(lang.message("silenced_interact"));
                event.setCancelled(true);
                return;
            }
        }
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        NamespacedKey randomKey = new NamespacedKey(plugin, "is_random_crystal");
        if (itemMeta.getPersistentDataContainer().has(randomKey, PersistentDataType.BYTE)) {
            item.setAmount(item.getAmount() - 1);
            List<String> crystalIds = new ArrayList<>(plugin.getCrystalManager().getCrystals().keySet());
            String randomCrystalId = crystalIds.get(random.nextInt(crystalIds.size()));
            Crystal newCrystal = plugin.getCrystalManager().getCrystal(randomCrystalId);
            player.getInventory().addItem(newCrystal.getItemStack());
            player.sendMessage(lang.message("random_transform", "display", newCrystal.getDisplayName()));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
            player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1);
            event.setCancelled(true);
            return;
        }
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(crystalKey, PersistentDataType.STRING)) {
            return;
        }
        String crystalId = container.get(crystalKey, PersistentDataType.STRING);
        Crystal crystal = plugin.getCrystalManager().getCrystal(crystalId);
        if (crystal == null) {
            return;
        }
        String cooldownKey = player.getUniqueId().toString() + ":" + crystalId;
        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(cooldownKey)) {
            long lastUse = cooldowns.get(cooldownKey);
            long cooldownTime = crystal.getCooldown() * 1000L;
            if (currentTime - lastUse < cooldownTime) {
                long remaining = (cooldownTime - (currentTime - lastUse)) / 1000;
                player.sendMessage(lang.message("cooldown", "seconds", String.valueOf(remaining)));
                return;
            }
        }
        switch (crystalId) {
            case "ates_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, LONG_TICKS, 0));
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);
            }
            case "buz_kristali" -> {
                player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation(), 100, 3, 3, 3, 0.2);
                player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof LivingEntity target && entity != player) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, SHORT_TICKS, 2));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, SHORT_TICKS, 1));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, SHORT_TICKS, 1));
                        target.getWorld().spawnParticle(Particle.SNOWFLAKE, target.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
                        target.damage(1.0, player);
                    }
                }
            }
            case "simsek_kristali" -> {
                LivingEntity targetEntity = null;
                double closestDist = Double.MAX_VALUE;
                for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
                    if (entity instanceof LivingEntity le && entity != player) {
                        double distance = player.getLocation().distanceSquared(entity.getLocation());
                        if (distance < closestDist) {
                            closestDist = distance;
                            targetEntity = le;
                        }
                    }
                }
                if (targetEntity != null) {
                    targetEntity.getWorld().strikeLightning(targetEntity.getLocation());
                    player.sendMessage(lang.message("lightning_hit"));
                } else {
                    player.sendMessage(lang.message("lightning_none"));
                    cooldowns.remove(cooldownKey);
                    return;
                }
            }
            case "yasam_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, SHORT_TICKS, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, SHORT_TICKS, 1));
                player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
            case "karanlik_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, MEDIUM_TICKS, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, MEDIUM_TICKS, 0));
                player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1.0f, 1.0f);
            }
            case "guc_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 240, 0));
                player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            }
            case "hiz_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, MEDIUM_TICKS, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, MEDIUM_TICKS, 0));
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 1.0f, 1.0f);
            }
            case "zirh_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 240, 0));
                player.getWorld().spawnParticle(Particle.BLOCK, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1, Material.IRON_BLOCK.createBlockData());
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 1.0f, 1.0f);
            }
            case "zehir_kristali" -> {
                player.getWorld().spawnParticle(Particle.ITEM_SLIME, player.getLocation(), 100, 3, 1, 3, 0.5);
                player.playSound(player.getLocation(), Sound.ENTITY_SPIDER_AMBIENT, 1.0f, 1.0f);
                for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
                    if (entity instanceof LivingEntity le && entity != player) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, SHORT_TICKS, 1));
                    }
                }
            }
            case "isik_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, MEDIUM_TICKS, 0));
                player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
            }
            case "ruh_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, SHORT_TICKS, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 240, 0));
                player.getWorld().spawnParticle(Particle.SOUL, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_AMBIENT, 1.0f, 1.0f);
            }
            case "zaman_kristali" -> {
                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 100, 4, 2, 4, 0.1);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof LivingEntity le && entity != player) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, SHORT_TICKS, 3));
                    }
                }
            }
            case "doga_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, MEDIUM_TICKS, 0));
                player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1.0f, 1.0f);
            }
            case "su_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, MEDIUM_TICKS, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, MEDIUM_TICKS, 0));
                player.getWorld().spawnParticle(Particle.BUBBLE, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.AMBIENT_UNDERWATER_ENTER, 1.0f, 1.0f);
            }
            case "toprak_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, MEDIUM_TICKS, 1));
                player.getWorld().spawnParticle(Particle.BLOCK, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1, Material.DIRT.createBlockData());
                player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 1.0f, 1.0f);
            }
            case "hava_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, MEDIUM_TICKS, 0));
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_SWOOP, 1.0f, 1.0f);
            }
            case "kan_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, SHORT_TICKS, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, SHORT_TICKS, 0));
                player.getWorld().spawnParticle(Particle.DUST, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1, new Particle.DustOptions(org.bukkit.Color.RED, 1.0F));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
            }
            case "olum_kristali" -> {
                boolean entityKilled = false;
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof LivingEntity target && entity != player) {
                        if (random.nextDouble() < 0.25) {
                            target.setHealth(0);
                            player.sendMessage(lang.message("death_kill"));
                        } else {
                            target.damage(15.0, player);
                            player.sendMessage(lang.message("death_injure"));
                        }
                        entityKilled = true;
                        break;
                    }
                }
                if (!entityKilled) {
                    player.sendMessage(lang.message("death_no_target"));
                }
                player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
            }
            case "isinlanma_kristali" -> {
                Block targetBlock = player.getTargetBlock(null, 20);
                if (targetBlock == null || targetBlock.getType() == Material.AIR) {
                    player.sendMessage(lang.message("tp_need_block"));
                    cooldowns.remove(cooldownKey);
                    return;
                }
                Location targetLocation = targetBlock.getLocation().add(0.5, 1, 0.5);
                targetLocation.setPitch(player.getLocation().getPitch());
                targetLocation.setYaw(player.getLocation().getYaw());
                Block feetBlock = targetLocation.getBlock();
                Block headBlock = feetBlock.getRelative(BlockFace.UP);
                if (!feetBlock.isPassable() || !headBlock.isPassable()) {
                    player.sendMessage(lang.message("tp_unsafe"));
                    cooldowns.remove(cooldownKey);
                    return;
                }
                player.teleport(targetLocation);
                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.2);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            }
            case "kaos_kristali" -> {
                List<LivingEntity> targets = new ArrayList<>();
                for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
                    if (entity instanceof LivingEntity le) {
                        targets.add(le);
                    }
                }
                targets.add(player);
                for (LivingEntity target : targets) {
                    PotionEffect randomEffect;
                    if (random.nextBoolean()) {
                        randomEffect = new PotionEffect(goodEffects.get(random.nextInt(goodEffects.size())), SHORT_TICKS, random.nextInt(2));
                    } else {
                        randomEffect = new PotionEffect(badEffects.get(random.nextInt(badEffects.size())), SHORT_TICKS, random.nextInt(2));
                    }
                    target.addPotionEffect(randomEffect);
                }
                player.getWorld().spawnParticle(Particle.ENCHANTED_HIT, player.getLocation(), 50, 1, 1, 1, 0.5);
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 1.0f);
            }
            case "madenci_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, MEDIUM_TICKS, 1));
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
                for (int x = -2; x <= 2; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -2; z <= 2; z++) {
                            Block block = player.getLocation().getBlock().getRelative(x, y, z);
                            Material type = block.getType();
                            if (minerBlocks.contains(type)) {
                                block.breakNaturally();
                                player.getWorld().spawnParticle(Particle.EXPLOSION, block.getLocation(), 1);
                            }
                        }
                    }
                }
            }
            case "buyucu_kristali" -> {
                Fireball fireball = player.launchProjectile(Fireball.class);
                fireball.setYield(1.5f);
                fireball.setIsIncendiary(false);
                fireball.setShooter(player);
                player.getWorld().spawnParticle(Particle.ENCHANTED_HIT, player.getLocation(), 30);
                player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f);
            }
            case "yercekimi_kristali" -> {
                for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
                    if (entity instanceof LivingEntity target && entity != player) {
                        target.setVelocity(new Vector(0, 1.5, 0));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 0));
                    }
                }
                player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation(), 50, 1, 1, 1, 0.2);
                player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_OPEN, 1.0f, 1.0f);
            }
            case "ayna_kristali" -> {
                mirrorCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 10000);
                player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 50);
                player.playSound(player.getLocation(), Sound.BLOCK_GLASS_PLACE, 1.0f, 1.5f);
            }
            case "hazine_kristali" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 300, 0));
                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 30, 0.5, 0.5, 0.5);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
            }
            case "metamorfoz_kristali" -> {
                boolean mobFound = false;
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof Monster monster) {
                        Location mobLoc = monster.getLocation();
                        monster.remove();
                        EntityType newType = passiveMobs.get(random.nextInt(passiveMobs.size()));
                        mobLoc.getWorld().spawnEntity(mobLoc, newType);
                        mobFound = true;
                        player.playSound(mobLoc, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0f, 1.2f);
                        player.getWorld().spawnParticle(Particle.HEART, mobLoc, 20);
                        break;
                    }
                }
                if (!mobFound) {
                    player.sendMessage(lang.message("no_monster"));
                }
            }
            case "sessizlik_kristali" -> {
                for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
                    if (entity instanceof Player target && entity != player) {
                        silenceCooldowns.put(target.getUniqueId(), System.currentTimeMillis() + 5000);
                        target.sendMessage(lang.message("silenced_you"));
                        target.playSound(target.getLocation(), Sound.ENTITY_WARDEN_NEARBY_CLOSER, 1.0f, 0.8f);
                    }
                }
                player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_AGITATED, 1.0f, 1.0f);
            }
            case "gunes_kristali" -> {
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 100, 3, 1, 3, 0.1);
                player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 0.8f);
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof LivingEntity le && entity != player) {
                        le.damage(10.0, player);
                        entity.setFireTicks(SHORT_TICKS);
                    }
                }
            }
            case "ay_kristali" -> {
                long worldTime = player.getWorld().getTime();
                if (worldTime > 13000 && worldTime < 23000) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, MEDIUM_TICKS, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, MEDIUM_TICKS, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, MEDIUM_TICKS, 0));
                    player.playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 1.0f, 1.0f);
                    player.getWorld().spawnParticle(Particle.WITCH, player.getLocation(), 50);
                } else {
                    player.sendMessage(lang.message("moon_night"));
                }
            }
        }
        cooldowns.put(cooldownKey, currentTime);
        player.sendMessage(lang.message("crystal_used", "display", crystal.getDisplayName()));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player damaged) {
            if (mirrorCooldowns.containsKey(damaged.getUniqueId())) {
                if (mirrorCooldowns.get(damaged.getUniqueId()) > System.currentTimeMillis()) {
                    if (event.getDamager() instanceof LivingEntity attacker) {
                        attacker.damage(event.getDamage() * 0.5);
                        damaged.getWorld().spawnParticle(Particle.CRIT, damaged.getLocation(), 20);
                    }
                } else {
                    mirrorCooldowns.remove(damaged.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSwitchItem(PlayerItemHeldEvent event) {
        Lang lang = plugin.getLang();
        Player player = event.getPlayer();
        if (silenceCooldowns.containsKey(player.getUniqueId()) && silenceCooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
            event.setCancelled(true);
            player.sendMessage(lang.message("silenced_switch"));
        } else {
            silenceCooldowns.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Lang lang = plugin.getLang();
        Player player = event.getPlayer();
        if (silenceCooldowns.containsKey(player.getUniqueId()) && silenceCooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
            event.setCancelled(true);
            player.sendMessage(lang.message("silenced_break"));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Lang lang = plugin.getLang();
        Player player = event.getPlayer();
        if (silenceCooldowns.containsKey(player.getUniqueId()) && silenceCooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
            event.setCancelled(true);
            player.sendMessage(lang.message("silenced_place"));
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Lang lang = plugin.getLang();
        Player player = event.getPlayer();
        if (silenceCooldowns.containsKey(player.getUniqueId()) && silenceCooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
            event.setCancelled(true);
            player.sendMessage(lang.message("silenced_drop"));
        }
    }

    @EventHandler
    public void onCraftRandomCrystal(CraftItemEvent event) {
        Lang lang = plugin.getLang();
        if (!event.getRecipe().getResult().isSimilar(plugin.getRandomCrystalItem())) {
            return;
        }
        CraftingInventory inv = event.getInventory();
        ItemStack[] matrix = inv.getMatrix();
        char[] shape = {'N', 'E', 'N', 'E', 'W', 'E', 'N', 'E', 'N'};
        for (int i = 0; i < matrix.length; i++) {
            ItemStack stack = matrix[i];
            char ingredientChar = shape[i];
            if (ingredientChar == 'E') {
                if (stack == null || stack.getAmount() < 12) {
                    event.setCancelled(true);
                    if (event.getWhoClicked() instanceof Player p) {
                        p.sendMessage(lang.message("craft_diamond"));
                    }
                    return;
                }
            }
            if (ingredientChar == 'N') {
                if (stack == null || stack.getAmount() < 3) {
                    event.setCancelled(true);
                    if (event.getWhoClicked() instanceof Player p) {
                        p.sendMessage(lang.message("craft_netherite"));
                    }
                    return;
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < matrix.length; i++) {
                    ItemStack stack = inv.getMatrix()[i];
                    if (stack != null) {
                        if (shape[i] == 'E') {
                            stack.setAmount(stack.getAmount() - 11);
                        } else if (shape[i] == 'N') {
                            stack.setAmount(stack.getAmount() - 2);
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}
