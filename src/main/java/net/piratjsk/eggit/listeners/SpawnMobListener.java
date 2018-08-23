package net.piratjsk.eggit.listeners;

import net.piratjsk.eggit.EggIt;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class SpawnMobListener implements Listener {

    @EventHandler
    public void onMobSpawnFromEgg(final PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getItem() == null) return;
        final ItemStack item = event.getItem();
        if (!item.getType().name().contains("_SPAWN_EGG")) return;
        event.setCancelled(true);
        final String typeName = item.getType().name().replace("_SPAWN_EGG", "");
        final EntityType type = EntityType.valueOf(typeName);
        final Location loc = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().add(0.5,0,0.5);
        final Entity entity = loc.getWorld().spawnEntity(loc, type);

        final ItemMeta egg = item.getItemMeta();

        if (egg.hasDisplayName())
            entity.setCustomName(egg.getDisplayName());

        EggIt.updateEntity(entity, item);

        if (entity instanceof Ageable) {
            ((Ageable) entity).setBaby();
        }

    }
}
