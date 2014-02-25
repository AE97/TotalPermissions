/*
 * Copyright (C) 2014 AE97
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
package net.ae97.totalpermissions.yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Set;
import net.ae97.totalpermissions.base.PermissionBase;
import net.ae97.totalpermissions.base.PermissionConsole;
import net.ae97.totalpermissions.base.PermissionEntity;
import net.ae97.totalpermissions.base.PermissionGroup;
import net.ae97.totalpermissions.base.PermissionOp;
import net.ae97.totalpermissions.base.PermissionRcon;
import net.ae97.totalpermissions.base.PermissionUser;
import net.ae97.totalpermissions.base.PermissionWorld;
import net.ae97.totalpermissions.data.DataHolder;
import net.ae97.totalpermissions.exceptions.DataLoadFailedException;
import net.ae97.totalpermissions.exceptions.DataSaveFailedException;
import net.ae97.totalpermissions.type.PermissionType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Lord_Ralex
 */
public class SplitYamlDataHolder implements DataHolder {

    private final File folder, userFile, groupFile, worldFile, entityFile, specialFile;
    private final YamlConfiguration userYamlConfiguration = new YamlConfiguration();
    private final YamlConfiguration groupYamlConfiguration = new YamlConfiguration();
    private final YamlConfiguration worldYamlConfiguration = new YamlConfiguration();
    private final YamlConfiguration entityYamlConfiguration = new YamlConfiguration();
    private final YamlConfiguration specialYamlConfiguration = new YamlConfiguration();
    private final EnumMap<PermissionType, HashMap<String, YamlPermissionBase>> cache = new EnumMap<PermissionType, HashMap<String, YamlPermissionBase>>(PermissionType.class);

    public SplitYamlDataHolder(File f) {
        folder = f;
        userFile = new File(folder, "users.yml");
        groupFile = new File(folder, "groups.yml");
        worldFile = new File(folder, "worlds.yml");
        entityFile = new File(folder, "entities.yml");
        specialFile = new File(folder, "specials.yml");
    }

    @Override
    public void load() throws DataLoadFailedException {
        try {
            try {
                userYamlConfiguration.load(userFile);
            } catch (FileNotFoundException ex) {
            }
            try {
                groupYamlConfiguration.load(groupFile);
            } catch (FileNotFoundException ex) {
            }
            try {
                worldYamlConfiguration.load(worldFile);
            } catch (FileNotFoundException ex) {
            }
            try {
                entityYamlConfiguration.load(entityFile);
            } catch (FileNotFoundException ex) {
            }
            try {
                specialYamlConfiguration.load(specialFile);
            } catch (FileNotFoundException ex) {
            }

        } catch (IOException ex) {
            throw new DataLoadFailedException(ex);
        } catch (InvalidConfigurationException ex) {
            throw new DataLoadFailedException(ex);
        }
    }

    @Override
    public void save(PermissionBase holder) throws DataSaveFailedException {
        holder.save();
        try {
            userYamlConfiguration.save(userFile);
        } catch (IOException ex) {
            throw new DataSaveFailedException(ex);
        }
        try {
            groupYamlConfiguration.save(groupFile);
        } catch (IOException ex) {
            throw new DataSaveFailedException(ex);
        }
        try {
            worldYamlConfiguration.save(worldFile);
        } catch (IOException ex) {
            throw new DataSaveFailedException(ex);
        }
        try {
            entityYamlConfiguration.save(entityFile);
        } catch (IOException ex) {
            throw new DataSaveFailedException(ex);
        }
        try {
            specialYamlConfiguration.save(specialFile);
        } catch (IOException ex) {
            throw new DataSaveFailedException(ex);
        }
    }

    @Override
    public void load(PermissionType type, String name) throws DataLoadFailedException {
        switch (type) {
            case USER:
                loadUser(name);
                break;
            case GROUP:
                loadGroup(name);
                break;
            case WORLD:
                loadWorld(name);
                break;
            case ENTITY:
                loadEntity(name);
                break;
            case OP:
                loadOp();
                break;
            case RCON:
                loadRcon();
                break;
            case CONSOLE:
                loadConsole();
                break;
        }
    }

    @Override
    public void loadUser(String name) throws DataLoadFailedException {
        ConfigurationSection sec = getData(userYamlConfiguration, name);
        YamlPermissionBase permBase = new YamlPermissionUser(name.toLowerCase(), sec);
        load(PermissionType.USER, permBase);
    }

    @Override
    public void loadGroup(String name) throws DataLoadFailedException {
        ConfigurationSection sec = getData(groupYamlConfiguration, name);
        YamlPermissionBase permBase = new YamlPermissionGroup(name.toLowerCase(), sec);
        load(PermissionType.GROUP, permBase);
    }

    @Override
    public void loadWorld(String name) throws DataLoadFailedException {
        ConfigurationSection sec = getData(worldYamlConfiguration, name);
        YamlPermissionBase permBase = new YamlPermissionWorld(name.toLowerCase(), sec);
        load(PermissionType.WORLD, permBase);
    }

    @Override
    public void loadEntity(String name) throws DataLoadFailedException {
        ConfigurationSection sec = getData(entityYamlConfiguration, name);
        YamlPermissionBase permBase = new YamlPermissionEntity(name.toLowerCase(), sec);
        load(PermissionType.ENTITY, permBase);
    }

    @Override
    public void loadConsole() throws DataLoadFailedException {
        ConfigurationSection sec = getData(specialYamlConfiguration, "console");
        YamlPermissionBase permBase = new YamlPermissionConsole(sec);
        load(PermissionType.CONSOLE, permBase);
    }

    @Override
    public void loadOp() throws DataLoadFailedException {
        ConfigurationSection sec = getData(specialYamlConfiguration, "op");
        YamlPermissionBase permBase = new YamlPermissionOp(sec);
        load(PermissionType.OP, permBase);
    }

    @Override
    public void loadRcon() throws DataLoadFailedException {
        ConfigurationSection sec = getData(specialYamlConfiguration, "rcon");
        YamlPermissionRcon permBase = new YamlPermissionRcon(sec);
        load(PermissionType.RCON, permBase);
    }

    @Override
    public PermissionBase get(PermissionType type, String name) throws DataLoadFailedException {
        switch (type) {
            case USER:
                return getUser(name);
            case GROUP:
                return getGroup(name);
            case WORLD:
                return getWorld(name);
            case ENTITY:
                return getEntity(name);
            case OP:
                return getOP();
            case CONSOLE:
                return getConsole();
            case RCON:
                return getRcon();
            default:
                return null;
        }
    }

    @Override
    public PermissionUser getUser(String name) throws DataLoadFailedException {
        checkCache(PermissionType.USER, name);
        return (PermissionUser) cache.get(PermissionType.USER).get(name.toLowerCase());
    }

    @Override
    public PermissionGroup getGroup(String name) throws DataLoadFailedException {
        checkCache(PermissionType.GROUP, name);
        return (PermissionGroup) cache.get(PermissionType.GROUP).get(name.toLowerCase());
    }

    @Override
    public PermissionWorld getWorld(String name) throws DataLoadFailedException {
        checkCache(PermissionType.WORLD, name);
        return (PermissionWorld) cache.get(PermissionType.WORLD).get(name.toLowerCase());
    }

    @Override
    public PermissionEntity getEntity(String name) throws DataLoadFailedException {
        checkCache(PermissionType.ENTITY, name);
        return (PermissionEntity) cache.get(PermissionType.ENTITY).get(name.toLowerCase());
    }

    @Override
    public PermissionOp getOP() throws DataLoadFailedException {
        checkCache(PermissionType.OP, null);
        return (PermissionOp) cache.get(PermissionType.OP).get(null);
    }

    @Override
    public PermissionConsole getConsole() throws DataLoadFailedException {
        checkCache(PermissionType.CONSOLE, null);
        return (PermissionConsole) cache.get(PermissionType.CONSOLE).get(null);
    }

    @Override
    public PermissionRcon getRcon() throws DataLoadFailedException {
        checkCache(PermissionType.GROUP, null);
        return (PermissionRcon) cache.get(PermissionType.RCON).get(null);
    }

    @Override
    public Set<String> getGroups() {
        return getList(groupYamlConfiguration);
    }

    @Override
    public Set<String> getUsers() {
        return getList(userYamlConfiguration);
    }

    @Override
    public Set<String> getWorlds() {
        return getList(worldYamlConfiguration);
    }

    @Override
    public Set<String> getEntities() {
        return getList(entityYamlConfiguration);
    }

    protected final ConfigurationSection getData(ConfigurationSection yamlConfiguration, String name) {
        ConfigurationSection sec = yamlConfiguration.getConfigurationSection(name);
        if (sec == null) {
            sec = yamlConfiguration.createSection(name);
        }
        return sec;
    }

    protected final Set<String> getList(ConfigurationSection yamlConfiguration) {
        return yamlConfiguration.getKeys(false);
    }

    protected void load(PermissionType type, YamlPermissionBase base) throws DataLoadFailedException {
        HashMap<String, YamlPermissionBase> baseMap = cache.get(type);
        base.load();
        if (baseMap == null) {
            baseMap = new HashMap<String, YamlPermissionBase>();
            cache.put(type, baseMap);
        }
        baseMap.put(null, base);
    }

    protected void checkCache(PermissionType type, String name) throws DataLoadFailedException {
        if (cache.get(type) == null || cache.get(type).get(name == null ? null : name.toLowerCase()) == null) {
            load(type, name);
        }
    }
}
