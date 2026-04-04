package xrefunsen.xcrystal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CrystalCommand implements CommandExecutor {

    private final XCrystalPlugin plugin;

    public CrystalCommand(XCrystalPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Lang lang = plugin.getLang();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.message("only_players"));
            return true;
        }
        if (!player.hasPermission("xcrystal.admin")) {
            player.sendMessage(lang.message("no_permission"));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(lang.message("cmd_header"));
            player.sendMessage(lang.message("cmd_line_get"));
            player.sendMessage(lang.message("cmd_example"));
            return true;
        }
        String crystalId = args[0].toLowerCase();
        if (crystalId.equals("random") || crystalId.equals("rastgele")) {
            player.getInventory().addItem(plugin.getRandomCrystalItem());
            player.sendMessage(lang.message("random_added"));
            return true;
        }
        Crystal crystal = plugin.getCrystalManager().getCrystal(crystalId);
        if (crystal == null) {
            player.sendMessage(lang.message("invalid_header"));
            for (String id : plugin.getCrystalManager().getCrystals().keySet()) {
                player.sendMessage("§7- " + id);
            }
            return true;
        }
        ItemStack stack = crystal.getItemStack();
        player.getInventory().addItem(stack);
        player.sendMessage(lang.message("crystal_added", "display", crystal.getDisplayName()));
        return true;
    }
}
