package net.piratjsk.eggit;

import net.piratjsk.eggit.listeners.CatchMobListener;
import net.piratjsk.eggit.listeners.SpawnMobListener;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.piratjsk.eggit.Util.colorize;
import static net.piratjsk.eggit.Util.decodeFromColors;
import static net.piratjsk.eggit.Util.decolorize;

public final class EggIt extends JavaPlugin {

    private final Map<EntityType, EggHandler> eggHandlers = new HashMap<>();
    private final List<EggHandler> genericEggHandlers = new ArrayList<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.registerListeners();
        this.registerEmptyEggRecipe();
        this.registerDefaultEggHandlers();
    }

    public static void updateEgg(final ItemStack egg, final Entity entity) {
        final EggIt plugin = JavaPlugin.getPlugin(EggIt.class);
        if (!plugin.eggHandlers.containsKey(entity.getType())) return;
        plugin.eggHandlers.get(entity.getType()).updateEgg(egg, entity);
        plugin.genericEggHandlers.forEach(handler -> handler.updateEgg(egg, entity));
    }

    public static void updateEntity(final Entity entity, final ItemStack egg) {
        final EggIt plugin = JavaPlugin.getPlugin(EggIt.class);
        if (!plugin.eggHandlers.containsKey(entity.getType())) return;
        plugin.eggHandlers.get(entity.getType()).updateEntity(entity, egg);
        plugin.genericEggHandlers.forEach(handler -> handler.updateEntity(entity, egg));
    }

    public static void registerEggHandler(final EntityType type, final EggHandler handler) {
        JavaPlugin.getPlugin(EggIt.class).eggHandlers.put(type,handler);
    }

    public static void registerGenericEggHandler(final EggHandler handler) {
        JavaPlugin.getPlugin(EggIt.class).genericEggHandlers.add(handler);
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new CatchMobListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SpawnMobListener(), this);
    }

    private void registerEmptyEggRecipe() {
        final ItemStack egg = new ItemStack(Material.EGG);
        final ItemMeta meta = egg.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Empty Egg");
        egg.setItemMeta(meta);
        final ShapedRecipe recipe = new ShapedRecipe(NamespacedKey.minecraft(this.getName().toLowerCase()), egg);
        recipe.shape(" I ", "IDI", " I ");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('D', Material.DIAMOND);
        this.getServer().addRecipe(recipe);
    }

    private void registerDefaultEggHandlers() {
        registerEggHandler(EntityType.SHEEP, new EggHandler() {
            @Override
            public void updateEgg(ItemStack egg, Entity entity) {
                final ItemMeta meta = egg.getItemMeta();
                final List<String> lore = new ArrayList<>();
                final Sheep sheep = (Sheep) entity;
                lore.add(colorize("&r&7Color: " + sheep.getColor().name().toLowerCase()));
                meta.setLore(lore);
                egg.setItemMeta(meta);
            }

            @Override
            public void updateEntity(Entity entity, ItemStack egg) {
                if (!egg.getItemMeta().hasLore()) return;
                final Sheep sheep = (Sheep) entity;
                final String colorName = decolorize(egg.getItemMeta().getLore().get(0).replace("Color: ", ""));
                final DyeColor color = DyeColor.valueOf(colorName.toUpperCase());
                sheep.setColor(color);
            }
        });
        registerEggHandler(EntityType.HORSE, new EggHandler() {
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

                final List<String> lore = new ArrayList<>();
                lore.add(colorize("&r&7Style: " + style.toLowerCase()));
                lore.add(colorize("&r&7Color: " + color.toLowerCase()));
                lore.add(colorize("&r&7Jump strength: " + Math.round(jumpHeight*100)/100 + " blocks&l" + encodedJumpStrength));
                lore.add(colorize("&r&7Speed: " + Math.round(bps*100)/100 + " blocks/sec&l" + encodedSpeed));

                final ItemMeta meta = egg.getItemMeta();
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
    }

}
