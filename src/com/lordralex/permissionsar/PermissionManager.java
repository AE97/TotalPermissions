package com.lordralex.permissionsar;

import com.lordralex.permissionsar.permission.PermissionGroup;
import com.lordralex.permissionsar.permission.PermissionUser;
import org.bukkit.entity.Player;

/**
 * @version 1.0
 * @author Joshua
 * @since 1.0
 */
public final class PermissionManager {

    private PermissionsAR plugin;

    /**
     * Creates a new instance of the PermissionsManager and sets the static
     * reference plugin instance to be the {@link PermissionsAR} instance
     * passed. This should only be called or created during the onEnable method
     * from the {@link PermissionsAR} class.
     *
     * @param aP The {@link PermissionsAR} plugin instance to use
     *
     * @since 1.0
     */
    public PermissionManager(PermissionsAR aP) {
        plugin = aP;
    }

    /**
     * Loads a player's {@link PermissionUser} object. This can return the one
     * that is stored in the cache if the player is online or has joined and
     * went offline.
     *
     * @param player The player's name to load
     * @return The {@link PermissionUser} for that player
     *
     * @since 1.0
     */
    public PermissionUser getUser(String player) {
        return PermissionUser.loadUser(player);
    }

    /**
     * Loads a player's {@link PermissionUser} object. This can return the one
     * that is stored in the cache if the player is online or has joined and
     * went offline.
     *
     * @param player The player's name to load
     * @return The {@link PermissionUser} for that player
     *
     * @since 1.0
     */
    public PermissionUser getUser(Player player) {
        return getUser(player.getName());
    }

    /**
     * Get's the {@link PermissionsAR} plugin instance.
     *
     * @return The {@link PermissionsAR} instance this class has stored.
     *
     * @since 1.0
     */
    public PermissionsAR getPlugin() {
        return plugin;
    }

    /**
     * Loads a group's {@link PermissionGroup} object. This can return the one
     * that is stored in the cache if there is one there.
     *
     * @param name The player's name to load
     * @return The {@link PermissionGroup} for that group
     *
     * @since 1.0
     */
    public PermissionGroup getGroup(String name) {
        return PermissionGroup.loadGroup(name);
    }
}
