package xyz.holocons.mc.resourceguard;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

public record ResourceRegion(String id, BlockVector3 pos1, BlockVector3 pos2, Map<Flag<?>, Object> flags) {

    public static Map<Flag<?>, Object> DEFAULT_FLAGS = Map.of(
            Flags.PVP, StateFlag.State.DENY,
            Flags.INVINCIBILITY, StateFlag.State.ALLOW,
            Flags.CREEPER_EXPLOSION, StateFlag.State.DENY,
            Flags.OTHER_EXPLOSION, StateFlag.State.DENY,
            Flags.MOB_SPAWNING, StateFlag.State.DENY
    );

    public ResourceRegion(String id, BlockVector3 loc1, BlockVector3 loc2) {
        this(id, loc1, loc2, DEFAULT_FLAGS);
    }

    public boolean isSimilar(ProtectedRegion region) {
        var expectedRegion = createRegion();

        return expectedRegion.getId().equals(region.getId()) &&
                expectedRegion.getMaximumPoint().equals(region.getMaximumPoint()) &&
                expectedRegion.getMinimumPoint().equals(region.getMinimumPoint()) &&
                hasSimilarFlags(region.getFlags());
    }

    public ProtectedRegion createRegion() {
        var region = new ProtectedCuboidRegion(id, pos1, pos2);
        region.setFlags(flags);
        region.getOwners().addGroup("staff");
        return region;
    }

    private boolean hasSimilarFlags(Map<Flag<?>, Object> comparedFlags) {
        return comparedFlags.entrySet().stream()
                .filter(entry -> flags().containsKey(entry.getKey()))
                .allMatch(entry -> flags().get(entry.getKey()).equals(entry.getValue()));
    }
}
