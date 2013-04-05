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
package com.lordralex.totalpermissions.permission;

import com.lordralex.totalpermissions.TotalPermissions;
import com.lordralex.totalpermissions.permission.util.PermissionUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

/**
 * Loads all permissions for every type of setup. This may load and try to find
 * excess information, such as inheritance in users.
 *
 * @since 0.1
 * @author Lord_Ralex
 * @version 0.1
 */
public abstract class PermissionBase {

    protected final String name;
    /**
     * @deprecated
     */
    protected final Map<String, Boolean> perms = new HashMap<String, Boolean>();
    protected final Map<String, Object> options = new HashMap<String, Object>();
    protected final ConfigurationSection section;
    protected final Map<String, Map<String, Boolean>> worldPerms = new HashMap<String, Map<String, Boolean>>();

    public PermissionBase(String aKey, String aName) {
        name = aName;

        TotalPermissions.getPlugin().getLogger().fine("Adding perms for " + aKey + "." + name);

        section = TotalPermissions.getPlugin().getPermFile().getConfigurationSection(aKey + "." + name);

        if (section != null) {
            List<String> permList = section.getStringList("permissions");
            if (permList != null) {
                for (String perm : permList) {
                    TotalPermissions.getPlugin().getLogger().fine("Adding perm: " + perm);
                    addPerm(perm);
                }
            }

            List<String> inherList = section.getStringList("inheritance");
            if (inherList != null) {
                for (String tempName : inherList) {
                    PermissionGroup tempGroup = TotalPermissions.getPlugin().getManager().getGroup(tempName);
                    List<String> tempGroupPerms = tempGroup.getPerms();
                    for (String perm : tempGroupPerms) {
                        addPerm(perm);
                    }
                }
            }

            List<String> groupList = section.getStringList("groups");
            if (groupList != null) {
                for (String tempName : groupList) {
                    PermissionGroup tempGroup = TotalPermissions.getPlugin().getManager().getGroup(tempName);
                    List<String> tempGroupPerms = tempGroup.getPerms();
                    for (String perm : tempGroupPerms) {
                        addPerm(perm);
                    }
                }
            }

            List<String> groupList2 = section.getStringList("group");
            if (groupList2 != null) {
                for (String tempName : groupList2) {
                    PermissionGroup tempGroup = TotalPermissions.getPlugin().getManager().getGroup(tempName);
                    List<String> tempGroupPerms = tempGroup.getPerms();
                    for (String perm : tempGroupPerms) {
                        addPerm(perm);
                    }
                }
            }

            ConfigurationSection optionSec = section.getConfigurationSection("options");
            if (optionSec != null) {
                Set<String> optionsList = optionSec.getKeys(true);
                for (String option : optionsList) {
                    options.put(option, optionSec.get(option));
                }
            }
            
            ConfigurationSection worldSec = section.getConfigurationSection("worlds");
            if (worldSec != null) {
                Set<String> worldList = worldSec.getKeys(false);
                for (String world : worldList) {
                    ConfigurationSection tempSection = TotalPermissions.getPlugin().getPermFile().getConfigurationSection(section + "." + world);
                    List<String> tempWorldPerms = tempSection.getStringList("permissions");
                    for (String perm : tempWorldPerms) {
                        addPerm(perm, world);
                    }
                }
            }


            //handles the loading of the commands aspect of the plugin
            List<String> commandList = section.getStringList("commands");
            if (commandList != null) {
                for (String command : commandList) {
                    PluginCommand cmd = Bukkit.getPluginCommand(command);
                    if (cmd == null) {
                        //removes a trailing / if possible
                        if (command.startsWith("/")) {
                            command = command.substring(1);
                            cmd = Bukkit.getPluginCommand(command);
                            if (cmd == null) {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                    perms.put(cmd.getPermission(), Boolean.TRUE);
                }
            }
        }
    }

    /**
     * Gets a list of the permissions for this group that are global. This
     * includes inherited perms. Negative perms start with a '-'.
     *
     * @return List of permissions with - in front of negative nodes
     *
     * @since 1.0
     */
    public synchronized List<String> getPerms() {
        return getPerms(null);
    }

    /**
     * Gets a list of the permissions for this group in the given world. This
     * includes inherited perms. Negative perms start with a '-'.
     *
     * @param world World to get perms
     *
     * @return List of permissions with - in front of negative nodes
     *
     * @since 1.0
     */
    public synchronized List<String> getPerms(String world) {
        List<String> permList = new ArrayList<String>();
        Map<String, Boolean> permMap = worldPerms.get(world);
        if (permMap == null || permMap.isEmpty()) {
            return new ArrayList<String>();
        }
        Map.Entry[] permKeys = permMap.entrySet().toArray(new Map.Entry[0]);
        for (Map.Entry entry : permKeys) {
            String perm = (String) entry.getKey();
            if (!((Boolean) entry.getValue()).booleanValue()) {
                perm = "-" + perm;
            }
            permList.add(perm);
        }
        return permList;
    }

    /**
     * Gets an option for the group. This is what is stored in the options:
     * section of the permissions in the groups
     *
     * @param key Path to option
     * @return Value of that option, or null if no option
     *
     * @since 1.0
     */
    public Object getOption(String key) {
        return options.get(key);
    }

    /**
     * Get the name of this group.
     *
     * @return Name of group
     *
     * @since 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Compares the name of the parameter with that of the group. If they match,
     * this will return true.
     *
     * @param base Name of another group
     * @return True if names match, false otherwise
     *
     * @since 1.0
     */
    public boolean equals(PermissionBase base) {
        if (base.getName().equalsIgnoreCase(name)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object val) {
        if (val instanceof PermissionBase) {
            return equals((PermissionBase) val);
        } else {
            return super.equals(val);
        }
    }

    /**
     * Add a permission node to the group. This will apply for adding negative
     * nodes too.
     *
     * @param perm Perm to add to this group
     */
    public final synchronized void addPerm(String perm) {
        addPerm(perm, null);
    }

    /**
     * Add a permission node to the group to this world. This will apply for
     * adding negative nodes too.
     *
     * @param perm Perm to add to this group
     * @param world World to have this perm affect
     */
    public final synchronized void addPerm(String perm, String world) {
        Map<String, Boolean> permList = worldPerms.get(world);
        if (permList == null) {
            permList = new HashMap<String, Boolean>();
        }
        if (perm.equals("**")) {
            List<String> allPerms = PermissionUtility.handleWildcard(true);
            for (String perm_ : allPerms) {
                if (!permList.containsKey(perm_)) {
                    permList.put(perm_, Boolean.TRUE);
                }
            }
        } else if (perm.equals("*")) {
            List<String> allPerms = PermissionUtility.handleWildcard(false);
            for (String perm_ : allPerms) {
                if (!permList.containsKey(perm_)) {
                    permList.put(perm_, Boolean.TRUE);
                }
            }
        } else if (perm.equals("-*")) {
            List<String> allPerms = PermissionUtility.handleWildcard(false);
            for (String perm_ : allPerms) {
                if (!permList.containsKey(perm_)) {
                    permList.put(perm_, Boolean.TRUE);
                }
            }
        } else if (perm.startsWith("-")) {
            permList.put(perm.substring(1), Boolean.FALSE);
        } else if (perm.startsWith("^")) {
            permList.put(perm.substring(1), Boolean.FALSE);
        } else {
            permList.put(perm, Boolean.TRUE);
        }
        worldPerms.put(world, permList);
    }

    /**
     * Checks to see if permission is given. This only checks the plugin-given
     * permissions
     *
     * @param perm Permission to check for
     * @return True if user/group has permission based on plugin
     */
    public synchronized boolean has(String perm) {
        return has(perm, null);
    }

    /**
     * Checks to see if permission is given. This only checks the plugin-given
     * permissions
     *
     * @param perm Permission to check for
     * @param world The world to check in
     * @return True if user/group has permission based on plugin
     */
    public synchronized boolean has(String perm, String world) {
        Map<String, Boolean> permList = worldPerms.get(world);
        if (permList == null || permList.isEmpty()) {
            return false;
        }
        Boolean result = permList.get(perm);
        if (result != null && result.booleanValue()) {
            return true;
        }
        return false;
    }

    /**
     * Adds the permissions for this PermissionBase to the given CommandSender.
     *
     * @param cs CommandSender to add the permissions to
     *
     * @since 1.0
     */
    public PermissionAttachment setPerms(CommandSender cs) {
        Map<String, Boolean> permList = worldPerms.get(null);
        if (permList == null) {
            permList = new HashMap<String, Boolean>();
        }
        PermissionAttachment attachment = cs.addAttachment(TotalPermissions.getPlugin());
        for (Entry entry : permList.entrySet()) {
            attachment.setPermission((String) entry.getKey(), (Boolean) entry.getValue());
        }
        if (cs instanceof Player) {
            Map<String, Boolean> worldList = worldPerms.get(((Player) cs).getWorld().getName());
            if (worldList == null) {
                worldList = new HashMap<String, Boolean>();
            }
            for (Entry entry : worldList.entrySet()) {
                attachment.setPermission((String) entry.getKey(), (Boolean) entry.getValue());
            }
        }
        return attachment;
    }
}
