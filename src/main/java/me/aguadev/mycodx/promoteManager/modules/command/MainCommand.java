package me.aguadev.mycodx.promoteManager.modules.command;

import me.aguadev.mycodx.promoteManager.Logger;
import me.aguadev.mycodx.promoteManager.utilities.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {
    Logger logger = Logger.getInstance();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            Utils.sendMessage(sender, logger.getLang().getString("global.only_players"));
            return true;
        }

        if (!player.hasPermission("promotemanager.admin")) {
            Utils.sendMessage(player, logger.getLang().getString("global.no_permissions"));
            return true;
        }

        if (args.length == 0) {
            Utils.sendMessage(player, "&cIncorrect usage.");
            return true;
        }

        String argument = args[0];
        if (argument.equalsIgnoreCase("reload")) {
            logger.getSettings().reload();
            logger.getLang().reload();

            Utils.sendMessage(player, logger.getLang().getString("global.config_reloaded"));
        } else {
            Utils.sendMessage(player, "&cIncorrect usage.");
        }

        return true;
    }
}
