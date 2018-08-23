package net.piratjsk.eggit;

import net.piratjsk.eggit.listeners.CatchMobListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class EggIt extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new CatchMobListener(this), this);
    }

}
