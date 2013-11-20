/*
 * Copyright (C) 2013 AE97
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
package net.ae97.totalpermissions.data;

import net.ae97.totalpermissions.permission.PermissionType;
import net.ae97.totalpermissions.sql.PermissionPersistance;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import net.ae97.totalpermissions.TotalPermissions;
import net.ae97.totalpermissions.lang.Lang;
import net.ae97.totalpermissions.util.DataHolderMerger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @version 1.0
 * @author Lord_Ralex
 */
public class MySQLDataHolder extends MemoryDataHolder {

    protected final EbeanServer ebeans;

    public MySQLDataHolder(EbeanServer server) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        if (server == null) {
            FileConfiguration cfg = TotalPermissions.getPlugin().getConfig();
            ServerConfig serverConfig = new ServerConfig();
            DataSourceConfig dataConfig = new DataSourceConfig();
            dataConfig.setDriver("com.mysql.jdbc.Driver");
            dataConfig.setUsername(cfg.getString("mysql.user", "lordralex"));
            dataConfig.setPassword(cfg.getString("mysql.pass", "password"));
            dataConfig.setUrl("jdbc:mysql://{ip}:{port}/{db}"
                    .replace("{ip}", cfg.getString("mysql.host", "localhost"))
                    .replace("{port}", cfg.getString("mysql.port", "3306"))
                    .replace("{db}", cfg.getString("mysql.database", "tp_db")));
            serverConfig.setDataSourceConfig(dataConfig);
            serverConfig.addClass(PermissionPersistance.class);
            serverConfig.setName("TotalPermissions");
            ClassLoader previous = Thread.currentThread().getContextClassLoader();
            Field field = Class.forName("org.bukkit.plugin.java.JavaPlugin").getDeclaredField("classLoader");
            field.setAccessible(true);
            ClassLoader classLoader = (ClassLoader) field.get(TotalPermissions.getPlugin());
            Thread.currentThread().setContextClassLoader(classLoader);
            server = EbeanServerFactory.create(serverConfig);
            Thread.currentThread().setContextClassLoader(previous);
        }
        ebeans = server;
    }

    @Override
    public void setup() {
        super.setup();
        TotalPermissions plugin = TotalPermissions.getPlugin();
        plugin.installDatabase(ebeans);
        if (new File(plugin.getDataFolder(), "mysql.yml").exists()) {
            plugin.log(Level.INFO, Lang.DATAHOLDER_MYSQL_IMPORT);
            try {
                File yamlFile = new File(plugin.getDataFolder(), "mysql.yml");
                YamlDataHolder yaml = new YamlDataHolder(yamlFile);
                yaml.setup();
                new DataHolderMerger(plugin, this).merge(yaml);
                File imports = new File(plugin.getDataFolder(), "imports");
                imports.mkdirs();
                yamlFile.renameTo(new File(imports, "mysql.yml"));
                yamlFile.delete();
                plugin.log(Level.INFO, Lang.DATAHOLDER_MYSQL_IMPORTCOMPLETE);
            } catch (InvalidConfigurationException ex) {
                plugin.log(Level.SEVERE, Lang.DATAHOLDER_MYSQL_IMPORTINVALID);
                plugin.getLogger().severe(ex.getMessage());
                plugin.debugLog(ex);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, Lang.ERROR_GENERIC.getMessage(), ex);
            }
        }

        Set<PermissionPersistance> groups = ebeans.find(PermissionPersistance.class).where().ieq("type", PermissionType.GROUPS.toString()).findSet();
        Map<String, ConfigurationSection> map = memory.get(PermissionType.GROUPS);
        for (PermissionPersistance group : groups) {
            if (map == null) {
                map = new HashMap<String, ConfigurationSection>();
            }
            try {
                map.put(group.getName().toLowerCase(), group.getConfig());
            } catch (InvalidConfigurationException ex) {
                plugin.log(Level.SEVERE, Lang.ERROR_CONFIG, "group." + group.getName());
                plugin.getLogger().severe(ex.getMessage());
                plugin.debugLog(ex);
            }
        }
        memory.put(PermissionType.GROUPS, map);
    }

    @Override
    public void load(PermissionType type, String name) throws InvalidConfigurationException {
        PermissionPersistance section = ebeans.find(PermissionPersistance.class).where().ieq("type", type.toString()).ieq("name", name).findUnique();
        if (section == null) {
            section = new PermissionPersistance();
            section.setName(name);
            section.setType(type);
            YamlConfiguration cfg = new YamlConfiguration();
            section.setConfig(cfg);
        }
        Map<String, ConfigurationSection> map = memory.get(type);
        if (map == null) {
            map = new HashMap<String, ConfigurationSection>();
        }
        map.put(name.toLowerCase(), section.getConfig());
        memory.put(type, map);
    }

    @Override
    public void save(PermissionType type, String name) {
        PermissionPersistance section = ebeans.find(PermissionPersistance.class).where().ieq("type", type.toString()).ieq("name", name).findUnique();
        if (section == null) {
            section = new PermissionPersistance();
            section.setName(name);
            section.setType(type);
        }
        Map<String, ConfigurationSection> map = memory.get(type);
        if (map == null) {
            map = new HashMap<String, ConfigurationSection>();
        }
        ConfigurationSection cfg = map.get(name.toLowerCase());
        if (cfg == null) {
            cfg = new YamlConfiguration();
        }
        section.setConfig(cfg);
        ebeans.save(section);
    }

    @Override
    public Set<String> getKeys(PermissionType type) {
        List<PermissionPersistance> set = ebeans.find(PermissionPersistance.class).where().ieq("type", type.toString()).findList();
        Set<String> names = new HashSet<String>();
        for (PermissionPersistance perm : set) {
            names.add(perm.getName());
        }
        return names;
    }
}
