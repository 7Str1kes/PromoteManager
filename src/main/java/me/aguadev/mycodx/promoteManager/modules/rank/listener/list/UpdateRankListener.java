package me.aguadev.mycodx.promoteManager.modules.rank.listener.list;

import me.aguadev.mycodx.promoteManager.Promote;
import me.aguadev.mycodx.promoteManager.modules.provider.Manager;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.UUID;

public class UpdateRankListener extends Manager implements Listener {

    public UpdateRankListener(Promote main) {
        super(main);
        registerLuckPermsListener();
    }

    private void registerLuckPermsListener() {
        EventBus eventBus = getMain().getLuckPerms().getEventBus();
        eventBus.subscribe(UserDataRecalculateEvent.class, this::onRankUpdate);
    }

    public void onRankUpdate(UserDataRecalculateEvent event) {
        User user = event.getUser();
        UUID playerUUID = user.getUniqueId();
        String playerName = Bukkit.getOfflinePlayer(playerUUID).getName();

        Optional<String> oldRankOpt = user.getNodes().stream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> ((InheritanceNode) node).getGroupName())
                .findFirst();

        String oldRank = oldRankOpt.orElse("Ninguno");



        String newRank = user.getPrimaryGroup();

        boolean isTemporary = user.getNodes().stream()
                .anyMatch(Node::hasExpiry);

        Bukkit.getLogger().info("El jugador " + playerName + " ha cambiado de rango.");
        Bukkit.getLogger().info("Antiguo rango: " + oldRank);
        Bukkit.getLogger().info("Nuevo rango: " + newRank);
        Bukkit.getLogger().info("Temporal: " + (isTemporary ? "Sí" : "No"));
    }
}