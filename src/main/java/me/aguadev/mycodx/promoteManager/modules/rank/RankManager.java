package me.aguadev.mycodx.promoteManager.modules.rank;

import lombok.Getter;
import me.aguadev.mycodx.promoteManager.Promote;
import me.aguadev.mycodx.promoteManager.modules.provider.Manager;
import me.aguadev.mycodx.promoteManager.utilities.Utils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class RankManager extends Manager {
    private final LuckPerms luckPerms;

    public RankManager(Promote main) {
        super(main);
        this.luckPerms = LuckPermsProvider.get();
    }

    public void promotePlayer(Player player, Player target) {
        List<String> rankList = getFiles().getSettings().getStringList("ranks");

        if (rankList.isEmpty()) {
            Utils.sendMessage(player, getFiles().getLang().getString("global.no_ranks"));
            return;
        }

        Collections.reverse(rankList);

        UUID targetUUID = target.getUniqueId();
        User user = luckPerms.getUserManager().getUser(targetUUID);

        if (user == null) {
            Utils.sendMessage(player, getFiles().getLang().getString("global.data_error"));
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
                Utils.sendMessage(player, getFiles().getLang().getString("promote_command.max_rank")
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

        Utils.sendMessage(player, getFiles().getLang().getString("promote_command.target_promoted")
                .replace("<player>", target.getName())
                .replace("<rank>", newRank));
        Utils.sendMessage(target, getFiles().getLang().getString("promote_command.promoted")
                .replace("<player>", player.getName())
                .replace("<rank>", newRank));
    }

    public void downgradePlayer(Player player, Player target) {
        List<String> rankList = getFiles().getSettings().getStringList("ranks");

        if (rankList.isEmpty()) {
            Utils.sendMessage(player, getFiles().getLang().getString("global.no_ranks"));
            return;
        }

        UUID targetUUID = target.getUniqueId();
        User user = luckPerms.getUserManager().getUser(targetUUID);

        if (user == null) {
            Utils.sendMessage(player, getFiles().getLang().getString("global.data_error"));
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
                Utils.sendMessage(player, getFiles().getLang().getString("downgrade_command.min_rank")
                        .replace("<player>", target.getName()));
                return;
            }

            newRank = rankList.get(index + 1);
            currentRank.ifPresent(rank -> user.data().remove(InheritanceNode.builder(rank).build()));
            user.data().add(InheritanceNode.builder(newRank).build());
        } else {
            Utils.sendMessage(player, getFiles().getLang().getString("downgrade_command.min_rank")
                    .replace("<player>", target.getName()));
            return;
        }

        luckPerms.getUserManager().saveUser(user);

        Utils.sendMessage(player, getFiles().getLang().getString("downgrade_command.target_downgraded")
                .replace("<player>", target.getName())
                .replace("<rank>", newRank));
        Utils.sendMessage(target, getFiles().getLang().getString("downgrade_command.downgraded")
                .replace("<player>", player.getName())
                .replace("<rank>", newRank));
    }

    public void demotePlayer(Player player, Player target) {
        List<String> rankList = getFiles().getSettings().getStringList("ranks");

        if (rankList.isEmpty()) {
            Utils.sendMessage(player, getFiles().getLang().getString("global.no_ranks"));
            return;
        }

        UUID targetUUID = target.getUniqueId();
        User user = luckPerms.getUserManager().getUser(targetUUID);

        if (user == null) {
            Utils.sendMessage(player, getFiles().getLang().getString("global.data_error"));
            return;
        }

        boolean removedAnyRank = false;
        for (Node node : user.getNodes()) {
            if (node.getKey().startsWith("group.")) {
                String rank = node.getKey().replace("group.", "");

                if (rankList.contains(rank)) {
                    user.data().remove(node);
                    removedAnyRank = true;
                }
            }
        }

        if (!removedAnyRank) {
            Utils.sendMessage(player, getFiles().getLang().getString("demote_command.no_ranks_to_demote")
                    .replace("<player>", target.getName()));
            return;
        }

        luckPerms.getUserManager().saveUser(user);

        Utils.sendMessage(player, getFiles().getLang().getString("demote_command.target_demoted")
                .replace("<player>", target.getName()));
        Utils.sendMessage(target, getFiles().getLang().getString("demote_command.demoted")
                .replace("<player>", player.getName()));
    }
}