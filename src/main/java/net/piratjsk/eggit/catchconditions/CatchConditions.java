package net.piratjsk.eggit.catchconditions;

import net.piratjsk.eggit.EggIt;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffectType;

public final class CatchConditions {

    public static void init() {
        EggIt.registerCatchCondition("age", (entity, player, data) -> {
            final String age = (String) data;
            if (!(entity instanceof Ageable)) return false;
            final Ageable ageable = (Ageable) entity;
            if (age.equalsIgnoreCase("baby"))
                return !ageable.isAdult();
            if (age.equalsIgnoreCase("adult"))
                return ageable.isAdult();
            return false;
        });
        EggIt.registerCatchCondition("maxSize", (entity, player, data) -> {
            final int size = Integer.valueOf((String)data, 0);
            if (size <= 0) return false;
            if (!(entity instanceof Slime)) return false;
            final Slime slime = (Slime) entity;
            return slime.getSize() <= size;
        });
        EggIt.registerCatchCondition("maxHealth", (entity, player, data) -> {
            final int health = Integer.valueOf((String)data, 0);
            if (health <= 0) return false;
            if (!(entity instanceof LivingEntity)) return false;
            final LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.getHealth() <= health;
        });
        EggIt.registerCatchCondition("weak", (entity, player, data) -> {
            final boolean weak = Boolean.valueOf((String)data);
            if (!(entity instanceof LivingEntity)) return false;
            final LivingEntity livingEntity = (LivingEntity) entity;
            return weak == (livingEntity.getPotionEffect(PotionEffectType.WEAKNESS) != null);
        });
    }

}
