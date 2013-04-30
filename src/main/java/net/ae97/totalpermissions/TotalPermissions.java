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
package net.ae97.totalpermissions;

import net.ae97.totalpermissions.runnable.UpdateRunnable;
import net.ae97.totalpermissions.commands.CommandHandler;
import net.ae97.totalpermissions.configuration.Configuration;
import net.ae97.totalpermissions.lang.Cipher;
import net.ae97.totalpermissions.listeners.TPListener;
import net.ae97.totalpermissions.mcstats.Metrics;
import net.ae97.totalpermissions.permission.util.FileUpdater;
import java.io.File;
import java.util.logging.Level;
import net.ae97.totalpermissions.api.TotalPermissionsAPI;
import net.ae97.totalpermissions.permission.util.FileConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @version 0.1
 * @author Lord_Ralex
 * @since 0.1
 */
public class TotalPermissions extends JavaPlugin {

    private String BUKKIT_VERSION = "NONE";
    private static final String[] ACCEPTABLE_VERSIONS = new String[]{
        "v1_5_R2",
        "v1_5_R1",
        "v1_4_R1"
    };
    protected FileConfiguration permFile;
    protected FileConfiguration configFile;
    protected PermissionManager manager;
    protected Configuration config;
    protected TPListener listener;
    protected Metrics metrics;
    protected CommandHandler commands;
    protected Cipher cipher;
    protected TotalPermissionsAPI apiKey;
    protected static boolean debug = false;

    @Override
    public void onLoad() {
        try {
            getLogger().info("Beginning initial preperations");

            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();

            }
            if (!(new File(getDataFolder(), "config.yml").exists())) {
                this.saveResource("config.yml", true);
            }
            if (!(new File(getDataFolder(), "permissions.yml").exists())) {
                this.saveResource("permissions.yml", true);
            }

            configFile = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
            config = new Configuration(this);
            config.loadDefaults();
            cipher = new Cipher(config.getString("language"));

            for (String version : ACCEPTABLE_VERSIONS) {
                try {
                    Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftHumanEntity");
                    BUKKIT_VERSION = version;
                    break;
                } catch (ClassNotFoundException e) {
                }
            }
            if (BUKKIT_VERSION.equalsIgnoreCase("NONE")) {
                getLogger().severe(this.getLangFile().getString("main.bad-version1"));
                getLogger().severe(this.getLangFile().getString("main.bad-version2"));
                config.disableReflection();
            }
            //force kill reflection, is buggy and I don't want it running now
            config.disableReflection();

            debug = config.getBoolean("angry-debug");

            permFile = new YamlConfiguration();
            try {
                permFile.load(new File(this.getDataFolder(), "permissions.yml"));
                FileUpdater update = new FileUpdater(this);
                update.backup(true);
                if (config.getBoolean("permissions.updater")) {
                    update.runUpdate();
                }
                if (config.getBoolean("permissions.formatter")) {
                    FileConverter converter = new FileConverter(permFile, new File(this.getDataFolder(), "permissions.yml"));
                    permFile = converter.convert();
                }
            } catch (InvalidConfigurationException e) {
                getLogger().log(Level.SEVERE, this.getLangFile().getString("main.yaml-error"));
                getLogger().log(Level.SEVERE, "-> " + e.getMessage());
                getLogger().log(Level.WARNING, this.getLangFile().getString("main.load-backup"));
                try {
                    permFile = new YamlConfiguration();
                    permFile.load(new File(this.getLastBackupFolder(), "permissions.yml"));
                    getLogger().log(Level.WARNING, this.getLangFile().getString("main.loaded1"));
                    getLogger().log(Level.WARNING, this.getLangFile().getString("main.loaded2"));
                } catch (InvalidConfigurationException e2) {
                    getLogger().log(Level.SEVERE, this.getLangFile().getString("main.load-failed1"));
                    getLogger().log(Level.SEVERE, this.getLangFile().getString("main.load-failed2"));
                    throw e2;
                }
            }

            apiKey = new TotalPermissionsAPI(this);

            getLogger().info("Initial preperations complete");
            if (config.getBoolean("update-check")) {
                Bukkit.getScheduler().runTaskLater(this, new UpdateRunnable(this), 1);
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, this.getLangFile().getString("main.error", getName(), this.getDescription().getVersion()), e);
            this.getPluginLoader().disablePlugin(this);
        }
    }

    @Override
    public void onEnable() {
        try {
            getLogger().config(this.getLangFile().getString("main.create.perms"));
            manager = new PermissionManager(this);
            manager.load();
            getLogger().config(this.getLangFile().getString("main.create.listener"));
            listener = new TPListener(this);
            Bukkit.getPluginManager().registerEvents(listener, this);
            getLogger().config(this.getLangFile().getString("main.create.command"));
            commands = new CommandHandler(this);
            getCommand("totalpermissions").setExecutor(commands);
            metrics = new Metrics(this);
            if (metrics.start()) {
                getLogger().info(this.getLangFile().getString("main.metrics"));
            }
        } catch (Exception e) {
            if (e instanceof InvalidConfigurationException) {
                getLogger().log(Level.SEVERE, this.getLangFile().getString("main.yaml-error"));
                getLogger().log(Level.SEVERE, ((InvalidConfigurationException) e).getMessage());
            } else {
                getLogger().log(Level.SEVERE, this.getLangFile().getString("main.error", getName(), this.getDescription().getVersion()), e);
            }
            this.getPluginLoader().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (manager != null) {
            manager.unload();
        }
    }

    /**
     * Gets the {@link PermissionManager} for this plugin.
     *
     * @return The {@link PermissionManager} that this is using
     *
     * @since 0.1
     */
    public PermissionManager getManager() {
        return this.manager;
    }

    /**
     * Gets the permissions file. This does not load the file, this only
     * provides the stored object.
     *
     * @return The perm file in the FileConfiguration form.
     *
     * @since 0.1
     */
    public FileConfiguration getPermFile() {
        return this.permFile;
    }

    /**
     * Gets the configuration file. This does not load the file, this only
     * provides the stored object.
     *
     * @return The configuration file in the FileConfiguration form.
     *
     * @since 0.1
     */
    public FileConfiguration getConfigFile() {
        return this.configFile;
    }

    /**
     * Gets the instance of the plugin.
     *
     * @return Instance of the plugin
     *
     * @since 0.1
     */
    public static TotalPermissions getPlugin() {
        return (TotalPermissions) Bukkit.getPluginManager().getPlugin("TotalPermissions");
    }

    /**
     * Gets the listener in use
     *
     * @return The Listener
     *
     * @since 0.1
     */
    public TPListener getListener() {
        return this.listener;
    }

    /**
     * Returns the {@link Configuration} that is loaded
     *
     * @return The {@link Configuration} in use
     *
     * @since 0.1
     */
    public Configuration getConfiguration() {
        return this.config;
    }

    /**
     * Returns the (@link Cipher) that is loaded
     *
     * @return the (@link Cipher) in use
     *
     * @since 0.2
     */
    public Cipher getLangFile() {
        return this.cipher;
    }

    /**
     * Returns the backup folder
     *
     * @return The backup folder
     *
     * @since 0.1
     */
    public File getBackupFolder() {
        return new File(this.getDataFolder(), "backups");
    }

    /**
     * Returns the location of the last folder used to back up perms. This may
     * not be exact, but uses the folder numbers.
     *
     * @return The File instance of the folder that contains the last backed up
     * files
     *
     * @since 0.1
     */
    public File getLastBackupFolder() {
        int highest = 0;
        File[] files = getBackupFolder().listFiles();
        if (files != null) {
            for (File file : files) {
                if (file == null) {
                    continue;
                }
                if (!file.isDirectory()) {
                    continue;
                }
                try {
                    int num = Integer.parseInt(file.getName());
                    if (highest < num) {
                        highest = num;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        File dir = new File(getBackupFolder(), Integer.toString(highest));
        if (dir.listFiles() == null || dir.listFiles().length == 0) {
            dir.delete();
            return getLastBackupFolder();
        }

        return new File(getBackupFolder(), Integer.toString(highest));
    }

    /**
     * Gets the Craftbukkit package version that the server is using
     *
     * @return Package CB/NMS files are in
     *
     * @since 0.1
     */
    public String getBukkitVersion() {
        return BUKKIT_VERSION;
    }

    /**
     * Gets the CommandHandler that is in use by this plugin
     *
     * @return The CommandHandler in use
     *
     * @since 0.1
     */
    public CommandHandler getCommandHandler() {
        return this.commands;
    }

    /**
     * Returns the (@link TotalPermissionsAPI) for TotalPermissions
     *
     * @return (@link TotalPermissionsAPI)
     *
     * @since 0.2
     */
    public TotalPermissionsAPI getAPI() {
        return this.apiKey;
    }

    /**
     * Gets the debug mode of the plugin. If this is true, the plugin is in
     * debug mode.
     *
     * @return True if in debug, otherwise false
     *
     * @since 0.1
     */
    public static boolean isDebugMode() {
        return debug;
    }
}
