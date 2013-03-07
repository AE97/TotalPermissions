/*
 * Copyright (C) 2013 LordRalex
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
package com.lordralex.totalpermissions.commands;

import com.lordralex.totalpermissions.commands.subcommands.*;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @version 0.1
 * @author Lord_Ralex
 * @since 0.1
 */
public final class CommandHandler implements CommandExecutor {

    protected final Map<String, SubCommand> commands = new HashMap<String, SubCommand>();

    public CommandHandler() {
        HelpCommand help = new HelpCommand();
        commands.put(help.getName().toLowerCase().trim(), help);
        DebugCommand debug = new DebugCommand();
        commands.put(debug.getName().toLowerCase().trim(), debug);
        ReloadCommand reload = new ReloadCommand();
        commands.put(reload.getName().toLowerCase().trim(), reload);
        BackupCommand backup = new BackupCommand();
        commands.put(backup.getName().toLowerCase().trim(), backup);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        String subCommand;
        if (args.length >= 1) {
            subCommand = args[0];
        } else {
            subCommand = "help";
        }
        SubCommand executor = commands.get(subCommand.toLowerCase());
        if (executor == null) {
            sender.sendMessage("No command found, use /totalperms help for command list");
            return true;
        }
        int length = args.length - 1;
        if (length < 0) {
            length = 0;
        }
        String[] newArgs = new String[length];
        for (int i = 0; i < newArgs.length; i++) {
            newArgs[i] = args[i + 1];
        }
        if (sender.hasPermission(executor.getPerm())) {
            executor.execute(sender, args);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You cannot use that command");
        }
        return true;
    }
}