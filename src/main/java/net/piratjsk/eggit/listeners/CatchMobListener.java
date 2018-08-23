package net.piratjsk.eggit.listeners;

import net.piratjsk.eggit.EggIt;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
            event.getPlayer().playSound(entity.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
        } else {
            event.getPlayer().playSound(entity.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
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

    private boolean canBeCaught(final Entity entity) {
        if (!spawnEggExists(entity)) return false;
        final List<Map<?,?>> mobs = this.plugin.getConfig().getMapList("mobs");
        for (final Map<?, ?> entry : mobs) {
            final Map<String,String> mob = (Map<String, String>) entry;
            if (!entity.getType().equals(EntityType.valueOf(mob.get("type").toUpperCase()))) continue;

            boolean mustBeBaby;
            if (mob.containsKey("baby")) {
                mustBeBaby = Boolean.valueOf(mob.get("baby"));
            } else {
                mustBeBaby = entity instanceof Ageable;
            }

            final boolean mustBeWeakened = mob.containsKey("weak") ? Boolean.valueOf(mob.get("weak")): false;
            final double maxHealth = mob.containsKey("maxHealth") ? Double.valueOf(mob.get("maxHealth")): 0;
            final int maxSize = mob.containsKey("maxSize") ? Integer.valueOf(mob.get("maxSize")): 0;

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

    private void catchMob(final Player player, final Entity mob) {
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (!isEmptyEgg(item)) return;
        item.setAmount(item.getAmount()-1);
        mob.remove();
        mob.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE,mob.getLocation(),2);
        final String egg = mob.getType().name() + "_spawn_egg";
        final ItemStack spawnEgg = new ItemStack(Material.getMaterial(egg.toUpperCase()));
        final ItemMeta meta = spawnEgg.getItemMeta();
        final List<String> lore = new ArrayList();

        if (mob.getType().equals(EntityType.SHEEP)) {
            final Sheep sheep = (Sheep) mob;
            lore.add(ChatColor.translateAlternateColorCodes('&',"&r&7Color: " + sheep.getColor().name().toLowerCase()));
        }

        meta.setLore(lore);
        spawnEgg.setItemMeta(meta);
        mob.getLocation().getWorld().dropItem(mob.getLocation(), spawnEgg);

    }
}
