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

import java.util.Arrays;
import java.util.List;
import net.ae97.totalpermissions.TotalPermissions;
import net.ae97.totalpermissions.permission.PermissionUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @since 0.2
 * @author 1Rogue
 * @version 0.2
 */
public class UserCommand implements SubCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 2) { // If there is an action command
            TotalPermissions.getPlugin().getCommandHandler().getActionHandler().onAction(sender, args, fields());
            return true;
        } else if ((args.length == 1) || args.length == 2) {
            Player p = null;
            if (args.length == 2) {
                p = Bukkit.getPlayer(args[1]);
            } else if (args.length == 1) {
                if (sender instanceof Player) {
                    p = (Player) sender;
                }
                else {
                    sender.sendMessage(TotalPermissions.getPlugin().getLangFile().getString("command.user.non-player"));
                    return false;
                }
            }
            PermissionUser user = TotalPermissions.getPlugin().getManager().getUser(p);
            StringBuilder sb = new StringBuilder();
            for (String group : user.getGroups(null)) {
                sb.append(group).append(", ");
            }
            sender.sendMessage(TotalPermissions.getPlugin().getLangFile().getString("command.user.player", sender.getName()));
            sender.sendMessage(TotalPermissions.getPlugin().getLangFile().getString("command.user.debug", user.getDebugState()));
            sender.sendMessage(TotalPermissions.getPlugin().getLangFile().getString("command.user.groups", sb.substring(0, sb.length() - 3)));
            return true;
        }
        return false;
    }

    public String getName() {
        return "user";
    }

    public String[] getHelp() {
        return new String[]{
            "/ttp user " + TotalPermissions.getPlugin().getLangFile().getString("variables.username") + " [actions..]",
            TotalPermissions.getPlugin().getLangFile().getString("command.user.help")
        };
    }

    private List<String> fields() {
        return Arrays.asList(new String[]{
            "permissions",
            "commands",
            "groups",
            "prefix",
            "suffix"
        });
    }
}