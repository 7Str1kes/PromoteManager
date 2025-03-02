package me.aguadev.mycodx.promoteManager.modules.rank.listener.list;

import me.aguadev.mycodx.promoteManager.Promote;
import me.aguadev.mycodx.promoteManager.modules.provider.Manager;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PermissionListener extends Manager implements Listener {

    private final Set<String> previousPermissions = new HashSet<>();

    public PermissionListener(Promote main) {
        super(main);
        registerLuckPermsListener();
    }

    private void registerLuckPermsListener() {
        EventBus eventBus = getMain().getLuckPerms().getEventBus();
        eventBus.subscribe(UserDataRecalculateEvent.class, this::onPermissionChange);
    }

    public void onPermissionChange(UserDataRecalculateEvent event) {
        User user = event.getUser();
        UUID playerUUID = user.getUniqueId();
        String playerName = Bukkit.getOfflinePlayer(playerUUID).getName();

        Set<String> currentPermissions = getStrings(user, playerName);

        previousPermissions.forEach(permission -> {
            if (!currentPermissions.contains(permission)) {
                Bukkit.getLogger().info("El jugador " + playerName + " ha perdido el permiso: " + permission);
            }
        });

        previousPermissions.clear();
        previousPermissions.addAll(currentPermissions);
    }

    private @NotNull Set<String> getStrings(User user, String playerName) {
        Set<String> currentPermissions = new HashSet<>();

        for (Node node : user.getNodes()) {
            if (node instanceof PermissionNode permissionNode) {
                currentPermissions.add(permissionNode.getPermission());
            }
        }

        currentPermissions.forEach(permission -> {
            if (!previousPermissions.contains(permission)) {
                boolean isTemporary = user.getNodes().stream()
                        .anyMatch(node -> node instanceof PermissionNode && node.hasExpiry());

                Bukkit.getLogger().info("El jugador " + playerName + " ha recibido el permiso: " + permission);
                Bukkit.getLogger().info("Temporal: " + (isTemporary ? "Sí" : "No"));
            }
        });
        return currentPermissions;
    }
}