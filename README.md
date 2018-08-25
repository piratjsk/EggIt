# EggIt
Catch mobs into spawn eggs.  
Caught mobs will preserve custom names and other specific traits like sheep color, villager profession or horse jump strength.

Inspired by [plugin by the same name](https://craftportal.pl/forum/index.php?/topic/59531-egg-it-kopia-zapasowa-zwierzątek/) from jCraft (now [openCraft](https://opencraft.pl)) minecraft server.

**Download**: https://github.com/piratjsk/EggIt/releases/latest  
**Issues**: https://github.com/piratjsk/EggIt/issues

### Building
```bash
git clone https://github.com/piratjsk/EggIt.git
cd EggIt
./gradlew clean
# ready jar file can now be found in ./build/libs
```

### API
#### Egg handlers
Egg handlers are responsible for saving mob traits to spawn egg items and giving them back to spawned mobs.

There are two types of egg handlers:
- entity type specific (applied only to mobs/eggs of specific [EntityType](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html))
- generic (applied to every mob/egg)

Each egg handler have unique id (for entity type specific it's entity type name), which allows to easily override or unregister each handler.

```java
EggIt.registerEggHandler(EntityType type, EggHandler handler);
EggIt.registerEggHandler(String id, EggHandler handler);
EggIt.unregisterEggHandler(EntityType type);
EggIt.unregisterEggHandler(String id);
```
Egg handler consist of two methods:
- `updateEgg(final ItemStack egg, final Entity entity)` which saves entity traits on to spawn egg item,
- `updateEntity(final Entity entity, final ItemStack egg)` which gives saved traits back to entity.

For example:
```java
EggIt.registerEggHandler("customName", new EggHandler() {
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
```
Applying egg handlers:
```java
EggIt.updateEgg(ItemStack egg, Entity entity); // saves entity traits to spawn egg item
EggIt.updateEntity(Entity entity, ItemStack egg); // gives traits saved on item back to entity
```

#### Catch conditions
...
```java
EggIt.registerCatchCondition(String id, CatchCondition condition);
EggIt.unregisterCatchCondition(String id);
EggIt.canBeCaught(Entity entity, Player player);
```

#### Empty Egg
```java
EggIt.getEmptyEgg(); // returns empty egg item which can be used by players to catch mobs.
```
