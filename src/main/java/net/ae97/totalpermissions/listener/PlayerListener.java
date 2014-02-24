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
package net.ae97.totalpermissions.listener;

import java.util.logging.Level;
import net.ae97.totalpermissions.TotalPermissions;
import net.ae97.totalpermissions.base.PermissionUser;
import net.ae97.totalpermissions.exceptions.DataLoadFailedException;
import net.ae97.totalpermissions.lang.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * 0.1
 *
 * @author Lord_Ralex
 * @since 0.1
 */
public class PlayerListener implements Listener {

    protected final TotalPermissions plugin;

    public PlayerListener(TotalPermissions p) {
        plugin = p;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        try {
            plugin.getDataManager().loadUser(event.getName());
        } catch (DataLoadFailedException ex) {
            plugin.log(Level.SEVERE, Lang.LISTENER_TPLISTENER_LOGIN_ERROR, ex);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        try {
            plugin.debugLog("PlayerLoginEvent fired, handling");
            PermissionUser user = plugin.getDataManager().getUser(event.getPlayer().getName());
            plugin.getDataManager().apply(user, event.getPlayer(), event.getPlayer().getWorld());
        } catch (DataLoadFailedException ex) {
            plugin.log(Level.SEVERE, "An error occured on the PlayerLoginEvent", ex);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        try {
            plugin.debugLog("PlayerJoinEvent-Lowest fired, handling");
            Player player = event.getPlayer();
            PermissionUser user = plugin.getDataManager().getUser(player.getName());
            plugin.getDataManager().apply(user, player, player.getWorld());
        } catch (DataLoadFailedException ex) {
            plugin.log(Level.SEVERE, "An error occured on the PlayerJoinEvent", ex);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinEventMonitor(PlayerJoinEvent event) {
        plugin.debugLog("PlayerJoinEvent-Monitor fired, handling");
        try {
            Player player = event.getPlayer();
            PermissionUser user = plugin.getDataManager().getUser(player.getName());
            plugin.getDataManager().apply(user, player, player.getWorld());
        } catch (DataLoadFailedException ex) {
            plugin.log(Level.SEVERE, "An error occured on the PlayerJoinEvent", ex);
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        try {
            plugin.debugLog("PlayerChangedWorldEvent fired, handling");
            Player player = event.getPlayer();
            PermissionUser user = plugin.getDataManager().getUser(player.getName());
            plugin.debugLog("Player " + player.getName() + " changed from " + event.getFrom().getName() + " to " + player.getWorld().getName());
            plugin.getDataManager().apply(user, player, player.getWorld());
        } catch (DataLoadFailedException ex) {
            plugin.log(Level.SEVERE, "An error occured on the PlayerChangedWorldEvent", ex);
        }
    }
}