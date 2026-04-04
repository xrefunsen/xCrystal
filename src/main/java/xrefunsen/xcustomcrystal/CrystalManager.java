package xrefunsen.xcustomcrystal;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CrystalManager {

    private final Map<String, Crystal> crystals = new HashMap<>();
    private final XCustomCrystalPlugin plugin;

    public CrystalManager(XCustomCrystalPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCrystals(Lang lang) {
        crystals.clear();
        ConfigurationSection root = lang.crystalsSection();
        for (String id : root.getKeys(false)) {
            ConfigurationSection c = root.getConfigurationSection(id);
            if (c == null) {
                continue;
            }
            String display = c.getString("display", id);
            List<String> lore = c.getStringList("lore");
            List<String> effects = c.getStringList("effects");
            crystals.put(id, new Crystal(id, display, Material.ECHO_SHARD, lore, 30, effects, plugin, lang));
        }
    }

    public Crystal getCrystal(String id) {
        return crystals.get(id);
    }

    public Map<String, Crystal> getCrystals() {
        return Collections.unmodifiableMap(crystals);
    }
}
