package me.aguadev.mycodx.promoteManager;

import lombok.Getter;
import me.aguadev.mycodx.promoteManager.modules.command.MainCommand;
import me.aguadev.mycodx.promoteManager.modules.provider.Files;
import me.aguadev.mycodx.promoteManager.modules.rank.RankManager;
import me.aguadev.mycodx.promoteManager.modules.rank.command.DowngradeCommand;
import me.aguadev.mycodx.promoteManager.modules.rank.command.PromoteCommand;
import me.aguadev.mycodx.promoteManager.modules.provider.Manager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Promote extends JavaPlugin {

    private LuckPerms luckPerms;
    private final Files files = new Files(this);
    private Manager manager;
    private RankManager rankManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (Exception e) {
            luckPerms = null;
        }

        rankManager = new RankManager(this);

        manager = new Manager(this);
        manager.load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}