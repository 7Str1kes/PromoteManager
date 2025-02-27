package me.aguadev.mycodx.promoteManager;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Promote extends JavaPlugin {

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        // Plugin startup logic
        new Logger(this);

        try {
            luckPerms = LuckPermsProvider.get();
        } catch (Exception e) {
            getLogger().severe("No se ha encontrado LuckPerms.");
            luckPerms = null;
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
