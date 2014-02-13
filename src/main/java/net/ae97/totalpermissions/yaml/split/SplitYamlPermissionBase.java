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
package net.ae97.totalpermissions.yaml.split;

import net.ae97.totalpermissions.base.PermissionBase;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Lord_Ralex
 */
public abstract class SplitYamlPermissionBase implements PermissionBase {

    protected final YamlConfiguration yamlConfiguration;
    private boolean debug = false;

    public SplitYamlPermissionBase(YamlConfiguration config) {
        yamlConfiguration = config;
    }

    @Override
    public final boolean isDebug() {
        return debug;
    }

    @Override
    public final boolean setDebug(boolean d) {
        return (debug = d);
    }
}
