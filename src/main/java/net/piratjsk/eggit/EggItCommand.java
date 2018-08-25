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

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("egg")) {
            this.giveEmptyEggCommand(sender, args);
            return true;
        }
        sender.sendMessage(colorize("[EggIt] /eggit egg [amount] [player]"));
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
