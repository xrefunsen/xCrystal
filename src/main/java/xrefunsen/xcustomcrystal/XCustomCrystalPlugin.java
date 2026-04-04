package xrefunsen.xcustomcrystal;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Locale;

public final class XCustomCrystalPlugin extends JavaPlugin {

    private CrystalManager crystalManager;
    private Lang lang;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        String code = getConfig().getString("language", "tr").toLowerCase(Locale.ROOT);
        if (!code.equals("tr") && !code.equals("eu")) {
            code = "tr";
        }
        lang = new Lang(this, code);
        crystalManager = new CrystalManager(this);
        crystalManager.registerCrystals(lang);
        getLogger().info(lang.message("enabled"));
        getServer().getPluginManager().registerEvents(new CrystalListener(this), this);
        getCommand("kristal").setExecutor(new CrystalCommand(this));
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "random_crystal"), getRandomCrystalItem());
        recipe.shape("NEN", "EWE", "NEN");
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('E', Material.DIAMOND_BLOCK);
        recipe.setIngredient('W', Material.NETHER_STAR);
        getServer().addRecipe(recipe);
    }

    @Override
    public void onDisable() {
        if (lang != null) {
            getLogger().info(lang.message("disabled"));
        }
    }

    public CrystalManager getCrystalManager() {
        return crystalManager;
    }

    public Lang getLang() {
        return lang;
    }

    public ItemStack getRandomCrystalItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(lang.randomCrystalName());
        meta.setLore(Arrays.asList(lang.randomCrystalLoreLine()));
        meta.getPersistentDataContainer().set(new NamespacedKey(this, "is_random_crystal"), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }
}
