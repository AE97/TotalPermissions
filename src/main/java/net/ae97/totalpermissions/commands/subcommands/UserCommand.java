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
import org.bukkit.command.CommandSender;

/**
 *
 * @since 0.2
 * @author 1Rogue
 * @version 0.2
 */
public class UserCommand implements SubCommand {

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("User command executed.");
        if (args.length > 2) { // If there is an action command
            TotalPermissions.getPlugin().getCommandHandler().getActionHandler().onAction(sender, args);
        }
        else if (args.length == 1) {
            //List all users
        }
        else {
            //Error in index length, return help
        }
    }

    public String getName() {
        return "user";
    }

    public String getPerm() {
        return "totalpermissions.command.user";
    }

    public String[] getHelp() {
        return new String[]{
            "/ttp user <username> [actions..]",
            "User interface"
        };
    }
}