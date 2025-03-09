package me.aguadev.mycodx.promoteManager.modules.rank.listener.list;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.aguadev.mycodx.promoteManager.Promote;
import me.aguadev.mycodx.promoteManager.modules.provider.Manager;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.time.Instant;
import java.util.*;

public class UpdateRankListener extends Manager implements Listener {
    private final Map<UUID, String> rankCache = new HashMap<>();
    private boolean started = false;

    public UpdateRankListener(Promote main) {
        super(main);
        registerLuckPermsListener();
        Bukkit.getScheduler().runTaskLater(main, this::loadOnlinePlayersRanks, 20L * 5);
    }

    private void registerLuckPermsListener() {
        EventBus eventBus = getMain().getLuckPerms().getEventBus();
        eventBus.subscribe(getMain(), UserDataRecalculateEvent.class, this::onRankUpdate);
    }

    private void loadOnlinePlayersRanks() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = getMain().getLuckPerms().getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                rankCache.put(player.getUniqueId(), user.getPrimaryGroup());
            }
        }
        started = true;
    }

    public void onRankUpdate(UserDataRecalculateEvent event) {
        User user = event.getUser();
        UUID playerUUID = user.getUniqueId();
        String playerName = Bukkit.getOfflinePlayer(playerUUID).getName();
        String newRank = user.getPrimaryGroup();
        String oldRank = rankCache.getOrDefault(playerUUID, "Ninguno");

        if (!started || oldRank.equals(newRank)) return;
        if (!rankCache.containsKey(playerUUID)) {
            rankCache.put(playerUUID, newRank);
            return;
        }

        rankCache.put(playerUUID, newRank);

        boolean isTemporary = user.getNodes().stream().anyMatch(Node::hasExpiry);

        this.sendLogRank(playerName, oldRank, newRank, isTemporary);
    }

    public void sendLogRank(String playerName, String oldRank, String newRank, boolean isTemporary) {
        if (newRank.equalsIgnoreCase("Ninguno")) return;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(getFiles().getSettings().getString("discord.webhook"));
            post.addHeader("Content-Type", "application/json");

            String descriptionTemplate = getFiles().getSettings().getString("discord.embeds.rank_update.description", "");
            String description = descriptionTemplate
                    .replace("<player>", playerName)
                    .replace("<previousRank>", oldRank.replace("default", "Usuario"))
                    .replace("<newRank>", newRank.replace("default", "Usuario"))
                    .replace("<isTemporary>", isTemporary ? "Yes" : "No");

            int color = getFiles().getSettings().getInt("discord.embeds.rank_update.color");

            JsonObject embed = new JsonObject();
            embed.addProperty("title", getFiles().getSettings().getString("discord.embeds.rank_update.title"));
            embed.addProperty("description", description);
            embed.addProperty("timestamp", Instant.now().toString());
            embed.addProperty("color", color);

            JsonObject footer = new JsonObject();
            footer.addProperty("text", "Rank Update Log");
            footer.addProperty("icon_url", "https://imgur.com/a/kdFrqpy");
            embed.add("footer", footer);

            JsonArray embeds = new JsonArray();
            embeds.add(embed);

            JsonObject json = new JsonObject();
            json.add("embeds", embeds);

            post.setEntity(new StringEntity(json.toString()));
            httpClient.execute(post);
        } catch (Exception e) {

        }
    }

//    public void sendLogRank(String playerName, String oldRank, String newRank, boolean isTemporary) {
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            String webhookUrl = getFiles().getSettings().getString("discord.webhook");
//            if (webhookUrl == null || webhookUrl.isEmpty()) {
//                Bukkit.getLogger().warning("Webhook URL is not set in the config!");
//                return;
//            }
//
//            int colorInt = getFiles().getSettings().getInt("discord.embeds.rank_update.color");
//
//            HttpPost post = new HttpPost(webhookUrl);
//            post.addHeader("Content-Type", "application/json");
//
//            String descriptionTemplate = getFiles().getSettings().getString("discord.embeds.rank_update.description", "");
//            String description = descriptionTemplate
//                    .replace("<player>", playerName)
//                    .replace("<previousRank>", oldRank.replace("default", "Usuario"))
//                    .replace("<newRank>", newRank.replace("default", "Usuario"))
//                    .replace("<isTemporary>", isTemporary ? "Yes" : "No");
//
//            JsonObject embed = new JsonObject();
//            embed.addProperty("title", getFiles().getSettings().getString("discord.embeds.rank_update.title", "🔔 Rank Updated"));
//            embed.addProperty("description", description);
//            embed.addProperty("timestamp", Instant.now().toString());
//            embed.addProperty("color", colorInt);
//
//            JsonObject footer = new JsonObject();
//            footer.addProperty("text", getFiles().getSettings().getString("discord.embeds.rank_update.footer", "© | PromoteManager"));
//            footer.addProperty("icon_url", getFiles().getSettings().getString("discord.embeds.rank_update.footer_icon", ""));
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