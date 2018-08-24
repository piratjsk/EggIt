package net.piratjsk.eggit;

import net.piratjsk.eggit.egghandlers.AnimalEggHandlers;
import net.piratjsk.eggit.egghandlers.GenericEggHandlers;
import net.piratjsk.eggit.listeners.CatchMobListener;
import net.piratjsk.eggit.listeners.SpawnMobListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
        AnimalEggHandlers.init();
        GenericEggHandlers.init();
    }

}
