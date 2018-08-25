package net.piratjsk.eggit;

import net.piratjsk.eggit.catchconditions.CatchConditions;
import net.piratjsk.eggit.egghandlers.AnimalEggHandlers;
import net.piratjsk.eggit.egghandlers.GenericEggHandlers;
import net.piratjsk.eggit.listeners.CatchMobListener;
import net.piratjsk.eggit.listeners.SpawnMobListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EggIt extends JavaPlugin {

    private final Map<String, EggHandler> eggHandlers = new HashMap<>();
    private final Map<String, CatchCondition> catchConditions = new HashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.registerListeners();
        this.registerEmptyEggRecipe();
        this.registerDefaultEggHandlers();
        this.registerDefaultCatchConditions();
        this.getCommand("eggit").setExecutor(new EggItCommand(this));
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new CatchMobListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SpawnMobListener(), this);
    }

    private void registerEmptyEggRecipe() {
        if (this.getConfig().isBoolean("recipe")) return;
        final ConfigurationSection recipeConfig = this.getConfig().getConfigurationSection("recipe");
        final ItemStack egg = EggIt.getEmptyEgg();
        final NamespacedKey nkey = new NamespacedKey(this, "empty_egg");
        if (recipeConfig.getKeys(false).contains("shape")) {
            final ShapedRecipe recipe = new ShapedRecipe(nkey, egg);
            recipe.shape(recipeConfig.getStringList("shape").toArray(new String[3]));
            recipeConfig.getConfigurationSection("ingredients").getKeys(false).forEach( key -> {
                final char ingredientKey = key.toCharArray()[0];
                final String ingredientTypeName = recipeConfig.getString("ingredients." + key).toUpperCase();
                final Material ingredientType = Material.getMaterial(ingredientTypeName);
                recipe.setIngredient(ingredientKey, ingredientType);
            });
            Bukkit.addRecipe(recipe);
        } else {
            final ShapelessRecipe recipe = new ShapelessRecipe(nkey, egg);
            recipeConfig.getStringList("ingredients").forEach(
                    ingredient -> recipe.addIngredient(Material.getMaterial(ingredient.toUpperCase()))
            );
            Bukkit.addRecipe(recipe);
        }
    }

    private void registerDefaultEggHandlers() {
        AnimalEggHandlers.init();
        GenericEggHandlers.init();
    }

    private void registerDefaultCatchConditions() {
        CatchConditions.init();
    }

    public static void registerEggHandler(final EntityType type, final EggHandler handler) {
        registerEggHandler(type.name(), handler);
    }

    public static void registerEggHandler(final String id, final EggHandler handler) {
        JavaPlugin.getPlugin(EggIt.class).eggHandlers.put(id, handler);
    }

    public static void unregisterEggHandler(final EntityType type) {
        unregisterEggHandler(type.name());
    }

    public static void unregisterEggHandler(final String id) {
        JavaPlugin.getPlugin(EggIt.class).eggHandlers.remove(id);
    }

    public static void registerCatchCondition(final String id, final CatchCondition condition) {
        JavaPlugin.getPlugin(EggIt.class).catchConditions.put(id, condition);
    }

    public static void unregisterCatchCondition(final String id) {
        JavaPlugin.getPlugin(EggIt.class).catchConditions.remove(id);
    }

    public static ItemStack getEmptyEgg() {
        final ItemStack egg = new ItemStack(Material.EGG);
        final ItemMeta meta = egg.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Empty Egg");
        egg.setItemMeta(meta);
        return egg;
    }

    public static void updateEgg(final ItemStack egg, final Entity entity) {
        final EggIt plugin = JavaPlugin.getPlugin(EggIt.class);
        plugin.eggHandlers.forEach((id, handler) -> {
            try {
                EntityType.valueOf(id.toUpperCase());
                // this is entity type specific egg handler
                if (!id.equalsIgnoreCase(entity.getType().name())) return; // handler is not for this type of entity
                handler.updateEgg(egg, entity);
            } catch (final IllegalArgumentException ignored) {
                // this is not entity type specific egg handler
                handler.updateEgg(egg, entity);
            }
        });
    }

    public static void updateEntity(final Entity entity, final ItemStack egg) {
        final EggIt plugin = JavaPlugin.getPlugin(EggIt.class);
        plugin.eggHandlers.forEach((id, handler) -> {
            try {
                EntityType.valueOf(id.toUpperCase());
                // this is entity type specific egg handler
                if (!id.equalsIgnoreCase(entity.getType().name())) return; // handler is not for this type of entity
                handler.updateEntity(entity, egg);
            } catch (final IllegalArgumentException ignored) {
                // this is not entity type specific egg handler
                handler.updateEntity(entity, egg);
            }
        });
    }

    public static boolean canBeCaught(final Entity entity, final Player player) {
        if (!spawnEggExists(entity)) return false;
        final EggIt plugin = JavaPlugin.getPlugin(EggIt.class);
        final String type = entity.getType().name();
        final List<Map<?, ?>> mobs = plugin.getConfig().getMapList("mobs");
        final Map<?,?> mobConfig = mobs.stream().filter(
                mob -> ((String)mob.get("type")).equalsIgnoreCase(type)).findFirst().orElse(Collections.emptyMap()
        );
        if (mobConfig.isEmpty()) return false;
        if (!mobConfig.containsKey("conditions")) return true;
        final Map<String, ?> conditions = (Map<String, ?>) mobConfig.get("conditions");
        for (final String conditionId : conditions.keySet()) {
            if (!plugin.catchConditions.containsKey(conditionId)) continue;
            if (!plugin.catchConditions.get(conditionId).check(entity, player, conditions.get(conditionId)))
                return false;
        }
        return true;
    }

    private static boolean spawnEggExists(final Entity entity) {
        final String egg = entity.getType().name() + "_spawn_egg";
        return Material.getMaterial(egg.toUpperCase()) != null;
    }

}
