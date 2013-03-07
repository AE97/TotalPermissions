/*
 * Copyright (C) 2013 LordRalex
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lordralex.totalpermissions;

import com.lordralex.totalpermissions.permission.PermissionGroup;
import com.lordralex.totalpermissions.permission.PermissionUser;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * @version 0.1
 * @author Lord_Ralex
 * @since 0.1
 */
public final class PermissionManager {

    protected Map<String, PermissionGroup> groups = new ConcurrentHashMap<String, PermissionGroup>();
    protected Map<String, PermissionUser> users = new ConcurrentHashMap<String, PermissionUser>();
    protected String defaultGroup = null;

    public PermissionManager() {
    }

    public void load() throws InvalidConfigurationException {
        FileConfiguration perms = TotalPermissions.getPlugin().getPermFile();
        ConfigurationSection filegroups = perms.getConfigurationSection("groups");
        if (groups == null) {
            throw new InvalidConfigurationException("You must define at least one group");
        }
        Set<String> allGroups = filegroups.getKeys(false);
        for (String group : allGroups) {
            PermissionGroup temp = new PermissionGroup(group);
            groups.put(group.toLowerCase(), temp);
            if (temp.isDefault()) {
                defaultGroup = temp.getName();
            }
        }
        if (defaultGroup == null) {
            throw new InvalidConfigurationException("You must define at least one default group");
        }
    }

    public void unload() {
        users.clear();
        groups.clear();
    }

    public String getDefaultGroup() {
        return defaultGroup;
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
    public synchronized PermissionUser getUser(String player) {
        PermissionUser user = users.get(player.toLowerCase());
        if (user == null) {
            user = new PermissionUser(player);
        }
        users.put(player.toLowerCase(), user);
        return user;
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
    public synchronized PermissionUser getUser(Player player) {
        PermissionUser user = users.get(player.getName().toLowerCase());
        if (user == null) {
            user = new PermissionUser(player);
        }
        users.put(player.getName().toLowerCase(), user);
        return user;
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
    public synchronized PermissionGroup getGroup(String name) {
        PermissionGroup group = groups.get(name.toLowerCase());
        if (group == null) {
            group = new PermissionGroup(name);
        }
        groups.put(name.toLowerCase(), group);
        return group;
    }

    /**
     *
     * @param player
     * @param perm
     * @return True if player has been given this perm, false otherwise
     */
    public synchronized boolean has(Player player, String perm) {
        return has(player.getName(), perm);
    }

    public synchronized boolean has(String player, String perm) {
        String name = player.toLowerCase();
        PermissionUser user = users.get(name);
        if (user == null) {
            user = new PermissionUser(name);
            users.put(name, user);
        }
        return user.has(perm);
    }

    public synchronized boolean groupHas(String groupName, String perm) {
        String name = groupName.toLowerCase();
        PermissionGroup group = groups.get(name);
        if (group == null) {
            group = new PermissionGroup(name);
            groups.put(name, group);
        }
        return false;
    }

    public synchronized void addPerm(Player player, String perm, boolean allowance) {
        String name = player.getName().toLowerCase();
        PermissionUser user = users.get(name);
        if (user == null) {
            user = new PermissionUser(name);
            users.put(name, user);
        }
        if (!allowance) {
            perm = "-" + perm;
        }
        user.addPerm(perm);
    }

    public synchronized void addPerm(OfflinePlayer player, String perm, boolean allowance) {
        String name = player.getName().toLowerCase();
        PermissionUser user = users.get(name);
        if (user == null) {
            user = new PermissionUser(name);
            users.put(name, user);
        }
        if (!allowance) {
            perm = "-" + perm;
        }
        user.addPerm(perm);
    }

    public synchronized void addPermToGroup(String group, String perm, boolean allowance) {
        String name = group.toLowerCase();
        PermissionGroup gr = groups.get(name);
        if (gr == null) {
            gr = new PermissionGroup(name);
            groups.put(name, gr);
        }
        if (!allowance) {
            perm = "-" + perm;
        }
        gr.addPerm(perm);
    }

    public synchronized void addPermToUser(String user, String perm, boolean allowance) {
        String name = user.toLowerCase();
        PermissionUser gr = users.get(name);
        if (gr == null) {
            gr = new PermissionUser(name);
            users.put(name, gr);
        }
        if (!allowance) {
            perm = "-" + perm;
        }
        gr.addPerm(perm);
    }
}