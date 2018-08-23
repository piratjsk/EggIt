package net.piratjsk.eggit.listeners;

import net.piratjsk.eggit.EggIt;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

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
        if (canBeCaught(entity)) {
            catchMob(event.getPlayer(), entity);
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
        return item.getType().equals(Material.EGG) && item.getItemMeta().getDisplayName().equals("Empty Egg");
    }

    private boolean canBeCaught(final Entity entity) {
        if (!spawnEggExists(entity)) return false;
        final ConfigurationSection config = this.plugin.getConfig().getConfigurationSection("mobsToCatch");
        for (final String mob : config.getKeys(false)) {
            if (!entity.getType().equals(EntityType.valueOf(mob.toUpperCase()))) continue;

            boolean mustBeBaby;
            if (config.isBoolean(mob + ".baby")) {
                mustBeBaby = config.getBoolean(mob + ".baby");
            } else {
                mustBeBaby = entity instanceof Ageable;
            }

            final boolean mustBeWeakened = config.getBoolean(mob + ".weak", false);
            final double maxHealth = config.getDouble(mob + ".maxHealth", 0);
            final int maxSize = config.getInt(mob + ".maxSize", 0);

            if (mustBeBaby) {
                final Ageable ageableEntity = (Ageable) entity;
                if (ageableEntity.isAdult()) return false;
            }

            if (mustBeWeakened) {
                final Collection<PotionEffect> effects = ((LivingEntity) entity).getActivePotionEffects();
                effects.removeIf(potionEffect -> !potionEffect.getType().equals(PotionEffectType.WEAKNESS));
                if (effects.isEmpty()) return false;
            }

            if (maxHealth > 0) {
                final LivingEntity livingEntity = ((LivingEntity) entity);
                if (livingEntity.getHealth() > maxHealth) return false;
            }

            if (maxSize > 0 && entity instanceof Slime) {
                final Slime slime = (Slime) entity;
                if (slime.getSize() > maxSize) return false;
            }

            return true;
        }
        return false;
    }

    private boolean spawnEggExists(final Entity entity) {
        final String egg = entity.getType().name() + "_spawn_egg";
        return Material.getMaterial(egg.toUpperCase()) != null;
    }
}
