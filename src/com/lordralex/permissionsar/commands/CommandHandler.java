package com.lordralex.permissionsar.commands;

import com.lordralex.permissionsar.commands.subcommands.SubCommand;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @version 1.0
 * @author Joshua
 * @since 1.0
 */
public class CommandHandler implements CommandExecutor {

    private Map<String, SubCommand> commands = new HashMap<String, SubCommand>();

    public CommandHandler() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        //TODO: 
        /*
         * What we will do here is just check args[0] is a sub-command, then 
         * execute that command's class in the 
         * com.lordralex.permissionsar.commands.subcommands class
         */
        String subCommand;
        if (args.length >= 1) {
            subCommand = args[0];
        } else {
            subCommand = "help";
        }
        SubCommand executor = commands.get(subCommand.toLowerCase());
        if (executor == null) {
            return false;
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
            //no permission to use this command
        }
        return true;
    }
}
