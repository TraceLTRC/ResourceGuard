package xyz.holocons.mc.resourceguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Bukkit;

import java.util.*;

public class ResourceRegionManager {

    private final ResourceGuard plugin;

    private Map<UUID, ResourceRegion> guardedRegions;

    public ResourceRegionManager(ResourceGuard plugin) {
        this.plugin = plugin;
        this.guardedRegions = new HashMap<>();
    }

    /**
     * Loads resource regions for each world in config. This does not actually create the regions.
     */
    public void loadRegionsFromFile() {
        if (!guardedRegions.isEmpty())
            guardedRegions.clear();

        var config = plugin.getConfig();
        var worlds = config.getConfigurationSection("worlds").getKeys(false);

        for (var world : worlds) {
            var bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld == null) {
                plugin.getLogger().warning("World " + world + " not found!");
                plugin.getLogger().warning("Will continue on without guarding said world.");
                continue;
            }

            String id = config.getString("worlds." + world + ".id");
            int xRad = config.getInt("worlds." + world + ".coordinates.x");
            int zRad = config.getInt("worlds." + world + ".coordinates.z");
            var spawn = bukkitWorld.getSpawnLocation();

            var pt1 = BlockVector3.at(spawn.getBlockX() - xRad, -64, spawn.getBlockZ() - zRad);
            var pt2 = BlockVector3.at(spawn.getBlockX() + xRad, 320, spawn.getBlockZ() + zRad);

            // TODO find out how to get flag values from config
            guardedRegions.put(bukkitWorld.getUID(), new ResourceRegion(id, pt1, pt2));
        }
        plugin.getLogger().info("Succesfully loaded " + guardedRegions.size() + " regions from disk!");
    }

    /**
     * Checks if existing regions matches with configuration. If it doesn't, delete the region and recreate it.
     */
    public void validateRegions() {
        plugin.getLogger().info("Validating regions...");
        for (var region : guardedRegions.entrySet()) {
            var regionManager = WorldGuard.getInstance().getPlatform()
                    .getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld(region.getKey())));

            if (regionManager == null) {
                plugin.getLogger().severe("World does not support regions, did you disable it?");
                plugin.getLogger().warning("Will skip validating world " + Bukkit.getWorld(region.getKey()).getName());
                continue;
            }

            if (!regionManager.hasRegion(region.getValue().id())) {
                plugin.getLogger().warning("Region " + region.getValue().id() + " doesn't exist in world " +
                        Bukkit.getWorld(region.getKey()).getName() + ". Creating a new region...");
                regionManager.addRegion(region.getValue().createRegion());
                continue;
            }

            var protectedRegion = regionManager.getRegion(region.getValue().id());

            if (!region.getValue().isSimilar(protectedRegion)) {
                regionManager.removeRegion(region.getValue().id());
                regionManager.addRegion(region.getValue().createRegion());
            } else {
                plugin.getLogger().info("Region " + region.getValue().id() + " fits with configuration! Continuing...");
            }
        }
        plugin.getLogger().info("Done validating!");
    }

    /**
     * Recreates all resource regions, regardless of similarity with configuration.
     */
    public void recreateRegions() {
        plugin.getLogger().info("Recreating all regions...");
        for (var region : guardedRegions.entrySet()) {
            var regionManager = WorldGuard.getInstance().getPlatform()
                    .getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld(region.getKey())));

            if (regionManager == null) {
                plugin.getLogger().severe("World does not support regions, did you disable it?");
                plugin.getLogger().warning("Will skip validating world " + Bukkit.getWorld(region.getKey()).getName());
                continue;
            }

            if (regionManager.hasRegion(region.getValue().id()))
                regionManager.removeRegion(region.getValue().id());

            regionManager.addRegion(region.getValue().createRegion());
            plugin.getLogger().info("Region " + region.getValue().id() + " added!");
        }
        plugin.getLogger().info("Done creating regions!");
    }
}
