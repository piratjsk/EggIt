package net.piratjsk.eggit;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public interface EggHandler {
    void updateEgg(final ItemStack egg, final Entity entity);
    void updateEntity(final Entity entity, final ItemStack egg);
}
