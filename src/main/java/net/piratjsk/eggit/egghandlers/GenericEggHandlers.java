package net.piratjsk.eggit.egghandlers;

import net.piratjsk.eggit.EggHandler;
import net.piratjsk.eggit.EggIt;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static net.piratjsk.eggit.Util.colorize;
import static net.piratjsk.eggit.Util.decolorize;

public class GenericEggHandlers {

    public static void init() {
        EggIt.registerGenericEggHandler(new EggHandler() {
            @Override
            public void updateEgg(final ItemStack egg, final Entity entity) {
                if (!(entity instanceof Ageable)) return;
                final Ageable ageable = (Ageable) entity;

                final String age = ageable.isAdult() ? "adult" : "baby";

                final ItemMeta meta = egg.getItemMeta();
                final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(colorize("&r&7Age: " + age));

                meta.setLore(lore);
                egg.setItemMeta(meta);
            }

            @Override
            public void updateEntity(final Entity entity, final ItemStack egg) {
                if (!egg.getItemMeta().hasLore()) return;
                if (!(entity instanceof Ageable)) return;
                final Ageable ageable = (Ageable) entity;

                for (final String line : egg.getItemMeta().getLore()) {
                    final String dline = decolorize(line);
                    if (dline.startsWith("Age: ")) {
                        final String age = dline.replace("Age: ", "");
                        if (age.equals("adult"))
                            ageable.setAdult();
                        else
                            ageable.setBaby();
                        break;
                    }
                }
            }
        });
        EggIt.registerGenericEggHandler(new EggHandler() {
            @Override
            public void updateEgg(final ItemStack egg, final Entity entity) {
                if (entity.getCustomName() == null) return;

                final ItemMeta meta = egg.getItemMeta();
                meta.setDisplayName(entity.getCustomName());
                egg.setItemMeta(meta);
            }
            @Override
            public void updateEntity(final Entity entity, final ItemStack egg) {
                if (!egg.getItemMeta().hasDisplayName()) return;
                entity.setCustomName(egg.getItemMeta().getDisplayName());
            }
        });
    }

}
