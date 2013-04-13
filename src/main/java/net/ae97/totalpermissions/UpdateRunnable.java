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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @version 0.1
 * @author Lord_Ralex
 */
public class UpdateRunnable extends BukkitRunnable {

    private static final String VERSION_URL = "https://raw.github.com/AE97/TotalPermissions/master/VERSION";
    private Boolean isLatest = null;
    private String latest;
    private String version;

    public UpdateRunnable() {
        super();
        version = TotalPermissions.getPlugin().getDescription().getVersion();
    }

    public void run() {
        if (version.endsWith("SNAPSHOT") || version.endsWith("DEV")) {
            TotalPermissions.getPlugin().getLogger().warning(TotalPermissions.getPlugin().getLangFile().getString("update.dev"));
            isLatest = true;
            return;
        }
        try {
            URL call = new URL(VERSION_URL);
            InputStream stream = call.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            latest = reader.readLine();
            reader.close();
            if (latest.equalsIgnoreCase(version)) {
                isLatest = true;
            } else {
                isLatest = false;
            }
        } catch (MalformedURLException ex) {
            TotalPermissions.getPlugin().getLogger().log(Level.SEVERE, TotalPermissions.getPlugin().getLangFile().getString("update.update-error"), ex);
        } catch (IOException ex) {
            TotalPermissions.getPlugin().getLogger().log(Level.SEVERE, TotalPermissions.getPlugin().getLangFile().getString("update.update-error"), ex);
        }
    }

    public boolean isUpdate() throws IllegalStateException {
        if (isLatest == null) {
            throw new IllegalStateException(TotalPermissions.getPlugin().getLangFile().getString("update.versioning-error"));
        }
        return !isLatest;
    }
}
