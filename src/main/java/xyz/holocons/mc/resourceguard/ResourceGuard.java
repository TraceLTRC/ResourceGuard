package xyz.holocons.mc.resourceguard;

import org.bukkit.plugin.java.JavaPlugin;

public final class ResourceGuard extends JavaPlugin {

    private ResourceRegionManager resourceRegionManager;

    @Override
    public void onLoad() {
        resourceRegionManager = new ResourceRegionManager(this);
    }

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        saveDefaultConfig();

        getCommand("res-guard").setExecutor(new CommandHandler(this));

        // Validate if current resource regions match the configuration
        resourceRegionManager.loadRegionsFromFile();
        resourceRegionManager.validateRegions();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ResourceRegionManager getResourceRegionManager() {
        return resourceRegionManager;
    }
}
