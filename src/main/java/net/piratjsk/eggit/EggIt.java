package net.piratjsk.eggit;

import net.piratjsk.eggit.listeners.CatchMobListener;
import net.piratjsk.eggit.listeners.SpawnMobListener;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EggIt extends JavaPlugin {

    private final Map<EntityType, EggHandler> eggHandlers = new HashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.registerListeners();
        this.registerEmptyEggRecipe();
        this.registerDefaultEggHandlers();
    }

    public static void updateEgg(final ItemStack egg, final Entity entity) {
        JavaPlugin.getPlugin(EggIt.class).eggHandlers.get(entity.getType()).updateEgg(egg, entity);
    }

    public static void updateEntity(final Entity entity, final ItemStack egg) {
        JavaPlugin.getPlugin(EggIt.class).eggHandlers.get(entity.getType()).updateEntity(entity, egg);
    }

    public static void registerEggHandler(final EntityType type, final EggHandler handler) {
        JavaPlugin.getPlugin(EggIt.class).eggHandlers.put(type,handler);
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
                lore.add(ChatColor.translateAlternateColorCodes('&',"&r&7Color: " + sheep.getColor().name().toLowerCase()));
                meta.setLore(lore);
                egg.setItemMeta(meta);
            }

            @Override
            public void updateEntity(Entity entity, ItemStack egg) {
                if (!egg.getItemMeta().hasLore()) return;
                final Sheep sheep = (Sheep) entity;
                final String colorName = ChatColor.stripColor(egg.getItemMeta().getLore().get(0).replace("Color: ", ""));
                final DyeColor color = DyeColor.valueOf(colorName.toUpperCase());
                sheep.setColor(color);
            }
        });
    }

}
