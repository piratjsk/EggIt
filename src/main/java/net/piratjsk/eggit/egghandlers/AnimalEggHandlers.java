package net.piratjsk.eggit.egghandlers;

import net.piratjsk.eggit.EggHandler;
import net.piratjsk.eggit.EggIt;
import net.piratjsk.eggit.Util;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static net.piratjsk.eggit.Util.colorize;
import static net.piratjsk.eggit.Util.decodeFromColors;
import static net.piratjsk.eggit.Util.decolorize;

public class AnimalEggHandlers {

    public static void init() {
        EggIt.registerEggHandler(EntityType.SHEEP, new EggHandler() {
            @Override
            public void updateEgg(ItemStack egg, Entity entity) {
                final Sheep sheep = (Sheep) entity;

                final String color = sheep.getColor().name();

                final ItemMeta meta = egg.getItemMeta();
                final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(colorize("&r&7Color: " + color.toLowerCase()));

                meta.setLore(lore);
                egg.setItemMeta(meta);
            }

            @Override
            public void updateEntity(Entity entity, ItemStack egg) {
                if (!egg.getItemMeta().hasLore()) return;
                final Sheep sheep = (Sheep) entity;
                for (final String line : egg.getItemMeta().getLore()) {
                    final String dline = decolorize(line);
                    if (dline.startsWith("Color: ")) {
                        final String colorName = dline.replace("Color: ", "");
                        final DyeColor color = DyeColor.valueOf(colorName.toUpperCase());
                        sheep.setColor(color);
                        break;
                    }
                }
            }
        });
        EggIt.registerEggHandler(EntityType.HORSE, new EggHandler() {
            @Override
            public void updateEgg(final ItemStack egg, final Entity entity) {
                final Horse horse = (Horse) entity;

                final String style = horse.getStyle().name();
                final String color = horse.getColor().name();
                final double jumpStrength = horse.getJumpStrength();
                final double jumpHeight = -0.1817584952 * Math.pow(jumpStrength, 3) + 3.689713992 * Math.pow(jumpStrength, 2) + 2.128599134 * jumpStrength - 0.343930367;
                final double speed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
                final double bps = speed * 43;

                final String encodedJumpStrength = Util.encodeAsColors(jumpStrength);
                final String encodedSpeed = Util.encodeAsColors(speed);

                final DecimalFormat df = new DecimalFormat("#.##");

                final ItemMeta meta = egg.getItemMeta();
                final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(colorize("&r&7Style: " + style.toLowerCase()));
                lore.add(colorize("&r&7Color: " + color.toLowerCase()));
                lore.add(colorize("&r&7Jump strength: " + df.format(jumpHeight) + " blocks&l" + encodedJumpStrength));
                lore.add(colorize("&r&7Speed: " + df.format(bps) + " blocks/sec&l" + encodedSpeed));

                meta.setLore(lore);
                egg.setItemMeta(meta);
            }
            @Override
            public void updateEntity(final Entity entity, final ItemStack egg) {
                if (!egg.getItemMeta().hasLore()) return;
                final Horse horse = (Horse) entity;

                for (final String line : egg.getItemMeta().getLore()) {
                    final String dline = decolorize(line);
                    if (dline.startsWith("Style: ")) {
                        final Horse.Style style = Horse.Style.valueOf(dline.replace("Style: ", "").toUpperCase());
                        horse.setStyle(style);
                    } else if (dline.startsWith("Color: ")) {
                        final Horse.Color color = Horse.Color.valueOf(dline.replace("Color: ", "").toUpperCase());
                        horse.setColor(color);
                    } else if (dline.startsWith("Jump strength: ")) {
                        final String encodedJumpStrength = line.split(ChatColor.BOLD.toString())[1];
                        final double jumpStrength = decodeFromColors(encodedJumpStrength);
                        horse.setJumpStrength(jumpStrength);
                    } else if (dline.startsWith("Speed: ")) {
                        final String encodedSpeed = line.split(ChatColor.BOLD.toString())[1];
                        final double speed = decodeFromColors(encodedSpeed);
                        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
                    }
                }
            }
        });
        EggIt.registerEggHandler(EntityType.OCELOT, new EggHandler() {
            @Override
            public void updateEgg(final ItemStack egg, final Entity entity) {
                final Ocelot ocelot = (Ocelot) entity;

                final String type = ocelot.getCatType().name();

                final ItemMeta meta = egg.getItemMeta();
                final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(colorize("&r&7Type: " + type.toLowerCase()));

                meta.setLore(lore);
                egg.setItemMeta(meta);
            }
            @Override
            public void updateEntity(final Entity entity, final ItemStack egg) {
                if (!egg.getItemMeta().hasLore()) return;
                final Ocelot ocelot = (Ocelot) entity;

                for (final String line : egg.getItemMeta().getLore()) {
                    final String dline = decolorize(line);
                    if (dline.startsWith("Type: ")) {
                        final Ocelot.Type type = Ocelot.Type.valueOf(dline.replace("Type: ", "").toUpperCase());
                        ocelot.setCatType(type);
                        break;
                    }
                }
            }
        });
        EggIt.registerEggHandler(EntityType.VILLAGER, new EggHandler() {
            @Override
            public void updateEgg(final ItemStack egg, final Entity entity) {
                final Villager villager = (Villager) entity;

                final String career = villager.getCareer().name();
                final String profession = villager.getProfession().name();
                final int riches = villager.getRiches();

                final ItemMeta meta = egg.getItemMeta();
                final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(colorize("&r&7Profession: " + profession.toLowerCase()));
                lore.add(colorize("&r&7Career: " + career.toLowerCase()));
                if (riches > 0)
                    lore.add(colorize("&r&7Riches: " + riches + " emeralds"));

                meta.setLore(lore);
                egg.setItemMeta(meta);
            }
            @Override
            public void updateEntity(final Entity entity, final ItemStack egg) {
                if (!egg.getItemMeta().hasLore()) return;
                final Villager villager = (Villager) entity;

                for (final String line : egg.getItemMeta().getLore()) {
                    final String dline = decolorize(line);
                    if (dline.startsWith("Career: ")) {
                        final Villager.Career career = Villager.Career.valueOf(dline.replace("Career: ", "").toUpperCase());
                        villager.setCareer(career);
                    } else if (dline.startsWith("Profession: ")) {
                        final Villager.Profession profession = Villager.Profession.valueOf(dline.replace("Profession: ", "").toUpperCase());
                        villager.setProfession(profession);
                    } else if (dline.startsWith("Riches: ")) {
                        final int riches = Integer.valueOf(dline.replace("Riches: ", "").replace(" emeralds", ""));
                        villager.setRiches(riches);
                    }
                }
            }
        });
    }

}
