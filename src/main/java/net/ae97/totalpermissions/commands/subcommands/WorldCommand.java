/*
 * Copyright (C) 2013 Spencer Alderman
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
import org.bukkit.command.CommandSender;

/**
 *
 * @since 0.2
 * @author 1Rogue
 * @version 0.2
 */
public class WorldCommand implements SubCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (args.length > 2) { // If there is an action command
            TotalPermissions.getPlugin().getCommandHandler().getActionHandler().onAction(sender, args, fields());
            return true;
        } else if (args.length == 1) {
            //List all users
            return true;
        }
        return false;
    }
    
    public String getName() {
        return "world";
    }

    public String[] getHelp() {
        return new String[] {
            "/ttp world <worldname> [actions..]",
            TotalPermissions.getPlugin().getLangFile().getString("command.world.help")
        };
    }
    
    private List<String> fields() {
        return Arrays.asList(new String[]{
            "inheritance"
        });
    }

}