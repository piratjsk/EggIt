package net.piratjsk.eggit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface CatchCondition {
    boolean check(final Entity entity, final Player player, final Object data);
}
