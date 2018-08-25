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
            return this.giveEmptyEggCommand(sender, args);
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("eggit.reload")) {
                sender.sendMessage(PREFIX + "&cYou can't do that.");
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(colorize(PREFIX + "&aConfig reloaded!"));
            return true;
        }
        return this.infoCommand(sender);
    }

    private boolean infoCommand(final CommandSender sender) {
        if (sender.hasPermission("eggit.give") || sender.hasPermission("eggit.reload")) {
            sender.sendMessage(colorize(PREFIX + "Commands:"));
            if (sender.hasPermission("eggit.give"))
                sender.sendMessage(colorize(PREFIX + "/eggit egg [amount] [player] &7- gives empty egg item"));
            if (sender.hasPermission("eggit.reload"))
                sender.sendMessage(colorize(PREFIX + "/eggit reload &7- reloads plugin configuration"));
        } else
            sender.sendMessage(colorize(PREFIX + "Eggs!"));
        return true;
    }

    private boolean giveEmptyEggCommand(final CommandSender sender, final String[] args) {
        if (!sender.hasPermission("eggit.give")) {
            sender.sendMessage(PREFIX + "&cYou can't do that.");
            return true;
        }
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
                return true;
            }
            sender.sendMessage(colorize("[EggIt] Can't find player '" + playerName + "'."));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize("[EggIt] Please specify player name: /eggit egg [amount] [player]"));
            return true;
        }

        final Player player = (Player) sender;
        player.getInventory().addItem(egg);
        return true;
    }

}
