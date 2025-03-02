package me.aguadev.mycodx.promoteManager.modules.provider;

import lombok.Getter;
import me.aguadev.mycodx.promoteManager.Promote;
import me.aguadev.mycodx.promoteManager.utilities.config.ConfigFile;

@Getter
public class Files{
    private final Promote main;
    public ConfigFile settings;
    public ConfigFile lang;

    public Files(Promote main) {
        this.main = main;
        createFiles();
    }

    public void createFiles() {
        settings = new ConfigFile(main, "settings.yml");
        lang = new ConfigFile(main, "language.yml");
    }
}
