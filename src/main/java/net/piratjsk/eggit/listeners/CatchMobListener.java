package net.piratjsk.eggit.listeners;

import net.piratjsk.eggit.EggIt;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class CatchMobListener implements Listener {

    private final EggIt plugin;

    public CatchMobListener(final EggIt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCatchMob(final PlayerInteractEntityEvent event) {
        final ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (!isEmptyEgg(item)) return;
        final Entity entity = event.getRightClicked();
        final Player player = event.getPlayer();
        if (EggIt.canBeCaught(entity, player)) {
            catchMob(player, entity);
            if (!plugin.getConfig().isBoolean("sounds") && !plugin.getConfig().isBoolean("sounds.success"))
                player.playSound(entity.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.success")), 1.0f, 1.0f);
        } else {
            if (!plugin.getConfig().isBoolean("sounds") && !plugin.getConfig().isBoolean("sounds.failure"))
                player.playSound(entity.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.failure")), 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onPlayerTryToThrowEmptyEgg(final PlayerInteractEvent event) {
        final ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (!isEmptyEgg(item)) return;
        final Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
        }

    }

    private boolean isEmptyEgg(final ItemStack item) {
        return item.getType().equals(Material.EGG) && item.getItemMeta().getDisplayName().equals(ChatColor.RESET + "Empty Egg");
    }

    private void catchMob(final Player player, final Entity entity) {
        this.takeOneEmptyEggFromPlayer(player);
        entity.remove();
        this.playMobCatchVisualEffectAt(entity.getLocation());
        final ItemStack egg = this.getSpawnEggFor(entity);
        EggIt.updateEgg(egg, entity);
        this.dropEggAt(egg, entity.getLocation());
    }

    private void takeOneEmptyEggFromPlayer(final Player player) {
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (!isEmptyEgg(item)) return;
        item.setAmount(item.getAmount()-1);
    }

    private void playMobCatchVisualEffectAt(final Location loc) {
        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE,loc,2);
    }

    private ItemStack getSpawnEggFor(final Entity entity) {
        final String eggItemTypeName = entity.getType().name() + "_spawn_egg";
        return new ItemStack(Material.getMaterial(eggItemTypeName.toUpperCase()));
    }

    private void dropEggAt(final ItemStack egg, final Location loc) {
       loc.getWorld().dropItem(loc, egg);
    }
}
