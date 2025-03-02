package me.aguadev.mycodx.promoteManager.modules.provider;

import lombok.Getter;
import lombok.Setter;
import me.aguadev.mycodx.promoteManager.Promote;
import me.aguadev.mycodx.promoteManager.modules.command.MainCommand;
import me.aguadev.mycodx.promoteManager.modules.rank.command.DemoteCommand;
import me.aguadev.mycodx.promoteManager.modules.rank.command.DowngradeCommand;
import me.aguadev.mycodx.promoteManager.modules.rank.command.PromoteCommand;
import me.aguadev.mycodx.promoteManager.modules.rank.listener.ListenerManager;
import me.aguadev.mycodx.promoteManager.modules.rank.RankManager;
import org.bukkit.event.Listener;

import java.util.Objects;

@Setter
@Getter
public class Manager {

    private final Promote main;
    private final Files files;

    // Managers
    //private RankManager rankManager;
    private ListenerManager listenerManager;

    public Manager(Promote main) {
        this.main = main;
        this.files = main.getFiles();
    }

    public void load() {
        //this.rankManager = new RankManager(main);
        this.listenerManager = new ListenerManager(main);

        this.loadCommands();
    }

    public void registerListener(Listener listener) {
        main.getServer().getPluginManager().registerEvents(listener, main);
    }

    public void loadCommands() {
        getMain().getCommand("promotemanager").setExecutor(new MainCommand(getMain()));
        getMain().getCommand("promote").setExecutor(new PromoteCommand(getMain()));
        getMain().getCommand("downgrade").setExecutor(new DowngradeCommand(getMain()));
        getMain().getCommand("demote").setExecutor(new DemoteCommand(getMain()));
    }
}