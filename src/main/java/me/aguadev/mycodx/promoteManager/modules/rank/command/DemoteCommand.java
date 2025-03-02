package me.aguadev.mycodx.promoteManager.modules.rank.command;

import me.aguadev.mycodx.promoteManager.Promote;
import me.aguadev.mycodx.promoteManager.modules.provider.Manager;
import me.aguadev.mycodx.promoteManager.modules.rank.RankManager;
import me.aguadev.mycodx.promoteManager.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DemoteCommand extends Manager implements CommandExecutor {
    public DemoteCommand(Promote main) {
        super(main);
    }

    public List<String> usage() {
        return getFiles().getLang().getStringList("demote_command.usage");
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            Utils.sendMessage(sender, getFiles().getLang().getString("global.only_players"));
            return true;
        }

        if (!player.hasPermission("promotemanager.promote")) {
            Utils.sendMessage(player, getFiles().getLang().getString("global.no_permissions"));
            return true;
        }

        if (args.length != 1) {
            Utils.sendMessage(player, String.join(" ", usage()));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Utils.sendMessage(player, getFiles().getLang().getString("global.player_not_found"));
            return true;
        }

        RankManager rankManager = getMain().getRankManager();
        if (rankManager == null) {
            Utils.sendMessage(player, "&cError: RankManager no está disponible");
            return true;
        }

        getMain().getRankManager().demotePlayer(player, target);

        return true;
    }
}
