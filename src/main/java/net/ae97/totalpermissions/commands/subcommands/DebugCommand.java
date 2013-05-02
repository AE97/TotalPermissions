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

import net.ae97.totalpermissions.TotalPermissions;
import net.ae97.totalpermissions.permission.PermissionUser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @since 0.1
 * @author Lord_Ralex
 * @version 0.1
 */
public class DebugCommand implements SubCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(TotalPermissions.getPlugin().getLangFile().getString("command.debug.args-plain"));
            return true; //True because currently the message contains help info
        }
        PermissionUser target = TotalPermissions.getPlugin().getManager().getUser(args[1]);
        if (target == null) {
            sender.sendMessage(TotalPermissions.getPlugin().getLangFile().getString("command.debug.null-target", args[1]));
            return true; //True because currently the message contains help info
        }
        if (args.length == 2 || args.length == 3) {
            if (args.length == 2) {
                String[] newArgs = new String[3];
                newArgs[0] = args[0];
                newArgs[1] = args[1];
                newArgs[2] = "toggle";
                args = newArgs;
            }
            boolean newState;
            if ((args[2].equalsIgnoreCase("on")) || (args[2].equalsIgnoreCase("enable")) || (args[2].equalsIgnoreCase("true"))) {
                newState = true;
            } else if (args[2].equalsIgnoreCase("toggle")) {
                newState = !target.getDebugState();
            } else {
                newState = false;
            }
            target.setDebug(newState);
            if (target.getDebugState()) {
                sender.sendMessage(ChatColor.GRAY + TotalPermissions.getPlugin().getLangFile().getString("command.debug.debug-on", target.getName()));
            } else {
                sender.sendMessage(ChatColor.GRAY + TotalPermissions.getPlugin().getLangFile().getString("command.debug.debug-off", target.getName()));
            }
        }
        return true;
    }

    public String getName() {
        return "debug";
    }

    public String getPerm() {
        return "totalpermissions.cmd.debug";
    }

    public String[] getHelp() {
        return new String[]{
            "/ttp debug " + TotalPermissions.getPlugin().getLangFile().getString("variables.username") + " [on/off]",
            TotalPermissions.getPlugin().getLangFile().getString("command.debug.help")
        };
    }
}
