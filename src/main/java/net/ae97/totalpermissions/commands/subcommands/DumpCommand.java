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
package net.ae97.totalpermissions.commands.subcommands;

import java.util.List;
import java.util.Set;
import net.ae97.totalpermissions.TotalPermissions;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

/**
 * @version 0.1
 * @author Lord_Ralex
 * @since 0.2
 */
public class DumpCommand implements SubCommand {

    private final int NUM_PAGE = 8;

    public String getName() {
        return "dumpperms";
    }

    public void execute(CommandSender sender, String[] args) {
        String[] params = new String[2];
        if (args.length == 1) {
            Player possible = Bukkit.getPlayer(args[0]);
            if (possible != null && possible.isOnline()) {
                params[0] = "-player";
            } else {
                if (Bukkit.getPluginCommand(args[0]) != null) {
                    params[0] = "-command";
                } else {
                    if (Bukkit.getPluginManager().getPlugin(args[0]) != null) {
                        params[0] = "-plugin";
                    } else {
                        sender.sendMessage(getHelp());
                        return;
                    }
                }
            }
            params[1] = args[0];
        }
        if (params[0].equalsIgnoreCase("-plugin")) {
            Plugin pl = Bukkit.getPluginManager().getPlugin(params[1]);
            List<Permission> perms = pl.getDescription().getPermissions();
            String[][] permPages = new String[perms.size() % NUM_PAGE][NUM_PAGE];
            int index = 0;
            for (int i = 0; i < perms.size(); i++) {
                if (i % NUM_PAGE == 0 && i != 0) {
                    index++;
                }
                permPages[index][i % NUM_PAGE] = "- " + perms.get(i).getName() + ": " + perms.get(i).getDefault().toString();
            }
            int page = 0;
            if (args.length == 3) {
                try {
                    page = Integer.getInteger(args[2]);
                } catch (NumberFormatException e) {
                    page = 0;
                }
            }
            if (page >= permPages.length) {
                page = 0;
            }
            sender.sendMessage(ChatColor.GREEN + "Perms for plugin: " + pl.getName());
            sender.sendMessage(ChatColor.GREEN + "Page " + (page + 1) + "/" + (permPages.length + 1));
            for (int i = 0; i < permPages[page].length; i++) {
                if (permPages[page][i] != null) {
                    sender.sendMessage(permPages[page][i]);
                }
            }
        } else if (params[0].equalsIgnoreCase("-player")) {
            Player player = Bukkit.getPlayer(params[1]);
            Set<PermissionAttachmentInfo> tempPerms = player.getEffectivePermissions();
            PermissionAttachmentInfo[] perms = tempPerms.toArray(new PermissionAttachmentInfo[tempPerms.size()]);
            String[][] permPages = new String[perms.length % NUM_PAGE][NUM_PAGE];
            int index = 0;
            for (int i = 0; i < perms.length; i++) {
                if (i % NUM_PAGE == 0 && i != 0) {
                    index++;
                }
                permPages[index][i % NUM_PAGE] = "- " + perms[0].getPermission() + ": " + perms[0].getValue();
            }
            int page = 0;
            if (args.length == 3) {
                try {
                    page = Integer.getInteger(args[2]);
                } catch (NumberFormatException e) {
                    page = 0;
                }
            }
            if (page >= permPages.length) {
                page = 0;
            }
            sender.sendMessage(ChatColor.GREEN + "Perms for player: " + player.getName());
            sender.sendMessage(ChatColor.GREEN + "Page " + (page + 1) + "/" + (permPages.length + 1));
            for (int i = 0; i < permPages[page].length; i++) {
                if (permPages[page][i] != null) {
                    sender.sendMessage(permPages[page][i]);
                }
            }
        } else if (params[0].equalsIgnoreCase("-command")) {
            Command command = Bukkit.getPluginCommand(params[0]);
            sender.sendMessage(ChatColor.GREEN + "Permission for " + command.getName() + ": " + command.getPermission());
        } else {
            sender.sendMessage(getHelp());
            return;
        }
    }

    public String getPerm() {
        return "totalpermissions.cmd.dump";
    }

    public String[] getHelp() {
        return new String[]{
            "/ttp backup [-command/-player/-plugin] [name] (page)",
            TotalPermissions.getPlugin().getLangFile().getString("command.dump.help")};
    }
}
