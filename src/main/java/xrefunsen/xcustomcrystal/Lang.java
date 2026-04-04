package xrefunsen.xcustomcrystal;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class Lang {

    private final FileConfiguration cfg;

    public Lang(JavaPlugin plugin, String code) {
        String file = "lang/" + code + ".yml";
        this.cfg = YamlConfiguration.loadConfiguration(
                new InputStreamReader(Objects.requireNonNull(plugin.getResource(file), "Missing " + file), StandardCharsets.UTF_8));
    }

    public String message(String path, String... replacements) {
        String s = cfg.getString("messages." + path, path);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            s = s.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        return s;
    }

    public String itemEffectsLabel() {
        return cfg.getString("item.effects_label", "§7Effects:");
    }

    public String itemCooldownLine(int seconds) {
        String s = cfg.getString("item.cooldown_line", "§7Cooldown: §c{seconds}s");
        return s.replace("{seconds}", String.valueOf(seconds));
    }

    public String randomCrystalName() {
        return cfg.getString("item.random_crystal_name", "§dRandom Crystal");
    }

    public String randomCrystalLoreLine() {
        return cfg.getString("item.random_crystal_lore", "§7Right-click to test your luck!");
    }

    public ConfigurationSection crystalsSection() {
        return Objects.requireNonNull(cfg.getConfigurationSection("crystals"), "crystals section missing");
    }
}
