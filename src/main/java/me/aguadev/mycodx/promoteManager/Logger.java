package me.aguadev.mycodx.promoteManager;

import lombok.Getter;
import me.aguadev.mycodx.promoteManager.modules.command.DowngradeCommand;
import me.aguadev.mycodx.promoteManager.modules.command.PromoteCommand;
import me.aguadev.mycodx.promoteManager.modules.manager.Manager;
import me.aguadev.mycodx.promoteManager.utilities.config.ConfigFile;

import java.util.Objects;

public class Logger {

    @Getter
    private static Logger instance;
    @Getter
    private final Promote main;

    @Getter
    public ConfigFile settings;
    @Getter
    public ConfigFile lang;

    @Getter
    Manager manager;

    public Logger(Promote plugin) {
        instance = this;
        main = plugin;

        createFiles();

        manager = new Manager();

        loadCommands();
    }

    public void createFiles() {
        settings = new ConfigFile(main, "settings.yml");
        lang = new ConfigFile(main, "language.yml");
    }

    public void loadCommands() {
        Objects.requireNonNull(main.getCommand("promote")).setExecutor(new PromoteCommand());
        Objects.requireNonNull(main.getCommand("downgrade")).setExecutor(new DowngradeCommand());
    }
}
