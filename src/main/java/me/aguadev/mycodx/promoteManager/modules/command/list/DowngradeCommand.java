package me.aguadev.mycodx.promoteManager.modules.command.list;

import me.aguadev.mycodx.promoteManager.Logger;
import me.aguadev.mycodx.promoteManager.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DowngradeCommand implements CommandExecutor {
    Logger logger = Logger.getInstance();
    public List<String> usage() {
        return logger.getLang().getStringList("downgrade_command.usage");
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Utils.sendMessage(sender, logger.getLang().getString("global.only_players"));
            return true;
        }

        if (!player.hasPermission("promotemanager.downgrade")) {
            Utils.sendMessage(player, logger.getLang().getString("global.no_permissions"));
            return true;
        }

        if (args.length != 1) {
            Utils.sendMessage(player, String.join(" ", usage()));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Utils.sendMessage(player, logger.getLang().getString("global.player_not_found"));
            return true;
        }

        logger.getManager().downgradePlayer(player, target);
        return true;
    }
}