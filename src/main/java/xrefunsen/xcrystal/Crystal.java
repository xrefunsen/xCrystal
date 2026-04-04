package xrefunsen.xcrystal;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class Crystal {

    private final String id;
    private final String displayName;
    private final Material material;
    private final List<String> lore;
    private final int cooldown;
    private final List<String> effects;
    private final XCrystalPlugin plugin;
    private final Lang lang;

    public Crystal(String id, String displayName, Material material, List<String> lore, int cooldown, List<String> effects,
                   XCrystalPlugin plugin, Lang lang) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.lore = lore;
        this.cooldown = cooldown;
        this.effects = effects;
        this.plugin = plugin;
        this.lang = lang;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getCooldown() {
        return cooldown;
    }

    public List<String> getEffects() {
        return effects;
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setDisplayName(displayName);
        List<String> fullLore = new ArrayList<>(lore);
        fullLore.add("");
        fullLore.add(lang.itemEffectsLabel());
        for (String effect : effects) {
            fullLore.add(effect);
        }
        fullLore.add("");
        fullLore.add(lang.itemCooldownLine(cooldown));
        meta.setLore(fullLore);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, "crystal_id"), PersistentDataType.STRING, id);
        item.setItemMeta(meta);
        return item;
    }
}
