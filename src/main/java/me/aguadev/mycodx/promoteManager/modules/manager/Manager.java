package me.aguadev.mycodx.promoteManager.modules.manager;

import lombok.Getter;
import me.aguadev.mycodx.promoteManager.Logger;
import me.aguadev.mycodx.promoteManager.utilities.Utils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Manager {
    private final LuckPerms luckPerms;
    Logger logger = Logger.getInstance();

    public Manager() {
        this.luckPerms = LuckPermsProvider.get();
    }

    public void promotePlayer(Player player, Player target) {
        List<String> rankList = Logger.getInstance().getSettings().getStringList("ranks");

        if (rankList.isEmpty()) {
            Utils.sendMessage(player, logger.getLang().getString("global.no_ranks"));
            return;
        }

        Collections.reverse(rankList);

        UUID targetUUID = target.getUniqueId();
        User user = luckPerms.getUserManager().getUser(targetUUID);

        if (user == null) {
            Utils.sendMessage(player, logger.getLang().getString("global.data_error"));
            return;
        }

        Optional<String> currentRank = user.getNodes().stream()
                .filter(node -> node.getKey().startsWith("group."))
                .map(node -> node.getKey().replace("group.", ""))
                .filter(rankList::contains)
                .findFirst();

        String newRank;

        if (currentRank.isPresent()) {
            int index = rankList.indexOf(currentRank.get());
            if (index == rankList.size() - 1) {
                Utils.sendMessage(player, logger.getLang().getString("promote_command.max_rank")
                        .replace("<player>", target.getName()));
                return;
            }
            newRank = rankList.get(index + 1);
        } else {
            newRank = rankList.get(0);
            user.data().remove(InheritanceNode.builder("default").build());
        }

        currentRank.ifPresent(rank -> user.data().remove(InheritanceNode.builder(rank).build()));

        user.data().add(InheritanceNode.builder(newRank).build());

        luckPerms.getUserManager().saveUser(user);

        Utils.sendMessage(player, logger.getLang().getString("promote_command.target_promoted")
                .replace("<player>", target.getName())
                .replace("<rank>", newRank));
        Utils.sendMessage(target, logger.getLang().getString("promote_command.promoted")
                .replace("<player>", player.getName())
                .replace("<rank>", newRank));
    }

    public void downgradePlayer(Player player, Player target) {
        List<String> rankList = Logger.getInstance().getSettings().getStringList("ranks");

        if (rankList.isEmpty()) {
            Utils.sendMessage(player, logger.getLang().getString("global.no_ranks"));
            return;
        }

        UUID targetUUID = target.getUniqueId();
        User user = luckPerms.getUserManager().getUser(targetUUID);

        if (user == null) {
            Utils.sendMessage(player, logger.getLang().getString("global.data_error"));
            return;
        }

        Optional<String> currentRank = user.getNodes().stream()
                .filter(node -> node.getKey().startsWith("group."))
                .map(node -> node.getKey().replace("group.", ""))
                .filter(rankList::contains)
                .findFirst();

        String newRank;

        if (currentRank.isPresent()) {
            int index = rankList.indexOf(currentRank.get());
            if (index == 0) {
                Utils.sendMessage(player, logger.getLang().getString("downgrade_command.min_rank")
                        .replace("<player>", target.getName()));
                return;
            }
            newRank = rankList.get(index - 1);
            currentRank.ifPresent(rank -> user.data().remove(InheritanceNode.builder(rank).build()));
            user.data().add(InheritanceNode.builder(newRank).build());
        } else {
            Utils.sendMessage(player, logger.getLang().getString("global.no_ranks"));
            return;
        }

        luckPerms.getUserManager().saveUser(user);

        Utils.sendMessage(player, logger.getLang().getString("downgrade_command.target_downgraded")
                .replace("<player>", target.getName())
                .replace("<rank>", newRank));
        Utils.sendMessage(target, logger.getLang().getString("downgrade_command.downgraded")
                .replace("<player>", player.getName())
                .replace("<rank>", newRank));
    }
}