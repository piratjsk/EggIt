package net.piratjsk.eggit;

import net.piratjsk.eggit.catchconditions.CatchConditions;
import net.piratjsk.eggit.egghandlers.AnimalEggHandlers;
import net.piratjsk.eggit.egghandlers.GenericEggHandlers;
import net.piratjsk.eggit.listeners.CatchMobListener;
import net.piratjsk.eggit.listeners.SpawnMobListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
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
        this.getCommand("eggit").setExecutor(new EggItCommand());
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
        if (!plugin.eggHandlers.containsKey(entity.getType().name())) return;
        plugin.eggHandlers.get(entity.getType().name()).updateEgg(egg, entity);
        plugin.eggHandlers.forEach((id, handler) -> handler.updateEgg(egg, entity));
    }

    public static void updateEntity(final Entity entity, final ItemStack egg) {
        final EggIt plugin = JavaPlugin.getPlugin(EggIt.class);
        if (!plugin.eggHandlers.containsKey(entity.getType().name())) return;
        plugin.eggHandlers.get(entity.getType().name()).updateEntity(entity, egg);
        plugin.eggHandlers.forEach((id, handler) -> handler.updateEntity(entity, egg));
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

    public static boolean canBeCaught(final Entity entity, final Player player) {
        if (!spawnEggExists(entity)) return false;
        final EggIt plugin = JavaPlugin.getPlugin(EggIt.class);
        final String type = entity.getType().name();
        final List<Map<?, ?>> mobs = plugin.getConfig().getMapList("mobs");
        final Map<?,?> mobConfig = mobs.stream().filter(mob -> ((String)mob.get("type")).equalsIgnoreCase(type)).findFirst().orElse(Collections.emptyMap());
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

    public static void registerCatchCondition(final String id, final CatchCondition condition) {
        JavaPlugin.getPlugin(EggIt.class).catchConditions.put(id, condition);
    }

    public static void unregisterCatchCondition(final String id) {
        JavaPlugin.getPlugin(EggIt.class).catchConditions.remove(id);
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new CatchMobListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SpawnMobListener(), this);
    }

    private void registerEmptyEggRecipe() {
        final ItemStack egg = EggIt.getEmptyEgg();
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

    private void registerDefaultCatchConditions() {
        CatchConditions.init();
    }

}
