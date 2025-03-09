package me.aguadev.mycodx.promoteManager.modules.rank.listener.list;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.aguadev.mycodx.promoteManager.Promote;
import me.aguadev.mycodx.promoteManager.modules.provider.Manager;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
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

        Set<String> currentPermissions = getStrings(user);

        previousPermissions.stream()
                .filter(permission -> !currentPermissions.contains(permission))
                .forEach(permission -> this.sendLogPermissionRemove(playerName, permission));

        currentPermissions.stream()
                .filter(permission -> !previousPermissions.contains(permission))
                .forEach(permission -> {
                    boolean isTemporary = user.getNodes().stream()
                            .anyMatch(node -> node instanceof PermissionNode && node.hasExpiry());
                    //this.sendLogPermissionAdd(playerName, permission, isTemporary);
                });

        previousPermissions.clear();
        previousPermissions.addAll(currentPermissions);
    }

    private @NotNull Set<String> getStrings(User user) {
        Set<String> currentPermissions = new HashSet<>();

        for (Node node : user.getNodes()) {
            if (node instanceof PermissionNode permissionNode) {
                currentPermissions.add(permissionNode.getPermission());
            }
        }

        return currentPermissions;
    }

    public void sendLogPermissionRemove(String playerName, String permission) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(getFiles().getSettings().getString("discord.webhook"));
            post.addHeader("Content-Type", "application/json");

            int color = getFiles().getSettings().getInt("discord.embeds.player_permission_remove.color");

            String descriptionTemplate = getFiles().getSettings().getString("discord.embeds.player_permission_remove.description", "");
            String description = descriptionTemplate
                    .replace("<player>", playerName)
                    .replace("<permission>", permission);

            JsonObject embed = new JsonObject();
            embed.addProperty("title", getFiles().getSettings().getString("discord.embeds.player_permission_remove.title"));
            embed.addProperty("description", description);
            embed.addProperty("timestamp", Instant.now().toString());
            embed.addProperty("color", color);

            JsonObject footer = new JsonObject();
            footer.addProperty("text", "Permission Removal Log");
            footer.addProperty("icon_url", "https://imgur.com/a/kdFrqpy");
            embed.add("footer", footer);

            JsonArray embeds = new JsonArray();
            embeds.add(embed);

            JsonObject json = new JsonObject();
            json.add("embeds", embeds);

            post.setEntity(new StringEntity(json.toString()));
            httpClient.execute(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLogPermissionAdd(String playerName, String permission, boolean isTemporary) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(getFiles().getSettings().getString("discord.webhook"));
            post.addHeader("Content-Type", "application/json");

            int color = getFiles().getSettings().getInt("discord.embeds.player_permission_add.color");

            String descriptionTemplate = getFiles().getSettings().getString("discord.embeds.player_permission_add.description", "");
            String description = descriptionTemplate
                    .replace("<player>", playerName)
                    .replace("<permission>", permission)
                    .replace("<isTemporary>", isTemporary ? "Yes" : "No");

            JsonObject embed = new JsonObject();
            embed.addProperty("title", "\uD83D\uDD2E | Permission Added");
            embed.addProperty("description", description);
            embed.addProperty("timestamp", Instant.now().toString());
            embed.addProperty("color", color); // Verde

            JsonObject footer = new JsonObject();
            footer.addProperty("text", "Permission Addition Log");
            footer.addProperty("icon_url", "https://imgur.com/a/kdFrqpy");
            embed.add("footer", footer);

            JsonArray embeds = new JsonArray();
            embeds.add(embed);

            JsonObject json = new JsonObject();
            json.add("embeds", embeds);

            post.setEntity(new StringEntity(json.toString()));
            httpClient.execute(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void sendLogPermissionRemove(String playerName, String permission) {
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            String webhookUrl = getFiles().getSettings().getString("discord.webhook");
//            if (webhookUrl == null || webhookUrl.isEmpty()) {
//                Bukkit.getLogger().warning("Webhook URL is not set in the config!");
//                return;
//            }
//
//            int colorInt = getFiles().getSettings().getInt("discord.embeds.player_permission_remove.color");
//
//            HttpPost post = new HttpPost(webhookUrl);
//            post.addHeader("Content-Type", "application/json");
//
//            JsonObject embed = new JsonObject();
//            embed.addProperty("title", getFiles().getSettings().getString("discord.embeds.player_permission_remove.title", "🚫 Permission Removed"));
//            embed.addProperty("description", getFiles().getSettings().getString("discord.embeds.player_permission_remove.description", "")
//                    .replace("<player>", playerName)
//                    .replace("<permission>", permission));
//            embed.addProperty("timestamp", Instant.now().toString());
//            embed.addProperty("color", colorInt);
//
//            JsonObject footer = new JsonObject();
//            footer.addProperty("text", getFiles().getSettings().getString("discord.embeds.player_permission_remove.footer", "© | PromoteManager"));
//            footer.addProperty("icon_url", getFiles().getSettings().getString("discord.embeds.player_permission_remove.footer_icon", ""));
//
//            embed.add("footer", footer);
//
//            JsonArray embeds = new JsonArray();
//            embeds.add(embed);
//
//            JsonObject json = new JsonObject();
//            json.add("embeds", embeds);
//
//            post.setEntity(new StringEntity(json.toString()));
//            httpClient.execute(post);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendLogPermissionAdd(String playerName, String permission, boolean isTemporary) {
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            String webhookUrl = getFiles().getSettings().getString("discord.webhook");
//            if (webhookUrl == null || webhookUrl.isEmpty()) {
//                Bukkit.getLogger().warning("Webhook URL is not set in the config!");
//                return;
//            }
//
//            int colorInt = getFiles().getSettings().getInt("discord.embeds.player_permission_add.color");
//
//            HttpPost post = new HttpPost(webhookUrl);
//            post.addHeader("Content-Type", "application/json");
//
//            JsonObject embed = new JsonObject();
//            embed.addProperty("title", getFiles().getSettings().getString("discord.embeds.player_permission_add.title", "✅ Permission Added"));
//            embed.addProperty("description", getFiles().getSettings().getString("discord.embeds.player_permission_add.description", "")
//                    .replace("<player>", playerName)
//                    .replace("<permission>", permission)
//                    .replace("<isTemporary>", isTemporary ? "Yes" : "No"));
//            embed.addProperty("timestamp", Instant.now().toString());
//            embed.addProperty("color", colorInt);
//
//            JsonObject footer = new JsonObject();
//            footer.addProperty("text", getFiles().getSettings().getString("discord.embeds.player_permission_add.footer", "© | PromoteManager"));
//            footer.addProperty("icon_url", getFiles().getSettings().getString("discord.embeds.player_permission_add.footer_icon", ""));
//
//            embed.add("footer", footer);
//
//            JsonArray embeds = new JsonArray();
//            embeds.add(embed);
//
//            JsonObject json = new JsonObject();
//            json.add("embeds", embeds);
//
//            post.setEntity(new StringEntity(json.toString()));
//            httpClient.execute(post);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}