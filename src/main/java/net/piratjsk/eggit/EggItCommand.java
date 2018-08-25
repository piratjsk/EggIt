package net.piratjsk.eggit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.piratjsk.eggit.Util.colorize;

public class EggItCommand implements CommandExecutor {

    private final EggIt plugin;
    private final static String PREFIX = "&7[&fEggIt&7] &r";

    EggItCommand(final EggIt plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("egg")) {
            this.giveEmptyEggCommand(sender, args);
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(colorize(PREFIX + "&aConfig reloaded!"));
            return true;
        }
        sender.sendMessage(colorize(PREFIX + "Commands:"));
        sender.sendMessage(colorize(PREFIX + "/eggit egg [amount] [player] &7- gives empty egg item"));
        sender.sendMessage(colorize(PREFIX + "/eggit reload &7- reloads plugin configuration"));
        return true;
    }

    private void giveEmptyEggCommand(final CommandSender sender, final String[] args) {
        final ItemStack egg = EggIt.getEmptyEgg();
        if (args.length > 1) {
            final int amount = Integer.valueOf(args[1]);
            if (amount > 1)
                egg.setAmount(amount);
        }
        if (args.length > 2) {
            final String playerName = args[2];
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (offlinePlayer.isOnline()) {
                final Player player = (Player) offlinePlayer;
                player.getInventory().addItem(egg);
                return;
            }
            sender.sendMessage(colorize("[EggIt] Can't find player '" + playerName + "'."));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize("[EggIt] Please specify player name: /eggit egg [amount] [player]"));
            return;
        }

        final Player player = (Player) sender;
        player.getInventory().addItem(egg);
    }

}
