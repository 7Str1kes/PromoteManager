package me.aguadev.mycodx.promoteManager.utilities.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ConfigFile extends YamlConfiguration {

    private final Plugin plugin;
    private java.io.File file;
    private FileConfiguration copy;


    public ConfigFile(final Plugin plugin, final String folderName, final String fileName) {
        this.plugin = plugin;

        try {
            load(folderName, fileName);
        } catch (IOException | InvalidConfigurationException exception) {
            plugin.getLogger().log(Level.WARNING, "A problem has occurred, the file " + file.getName() + " not create. Notify the developer", exception);
        }
    }

    public ConfigFile(final Plugin plugin, final String fileName) {
        this.plugin = plugin;

        try {
            load(null, fileName);
        } catch (IOException | InvalidConfigurationException exception) {
            plugin.getLogger().log(Level.WARNING, "A problem has occurred, the file " + file.getName() + " not create. Notify the developer", exception);
        }
    }


    public void load(final String folderName, final String fileName) throws IOException, InvalidConfigurationException {

        if (folderName == null) {

            file = new java.io.File(plugin.getDataFolder(), fileName.endsWith(".yml") ? fileName : fileName + ".yml");

            if (!file.exists()) {

                if (plugin.getResource(file.getName()) != null) {
                    plugin.saveResource(file.getName(), true);
                } else {
                    file.createNewFile();
                }

                plugin.getLogger().log(Level.INFO, file.toString());
            }

        } else {

            file = new java.io.File(plugin.getDataFolder() + java.io.File.separator + folderName);
            if (!file.exists()) file.mkdirs();

            String fileN = fileName.endsWith(".yml") ? fileName : fileName + ".yml";
            file = new java.io.File(plugin.getDataFolder() + java.io.File.separator + folderName, fileN);

            if (!file.exists()) {

                if (plugin.getResource(folderName + java.io.File.separator + fileN) != null) {
                    plugin.saveResource(folderName + java.io.File.separator + fileN, true);

                } else {
                    file.createNewFile();
                }

                plugin.getLogger().log(Level.INFO, file.toString());
            }
        }

        this.load(file);
    }


    /* Fail-proof methods */
    public void setCopyResource() {
        String filter = file.toString().replaceAll("plugins/", "").replaceAll(plugin.getName() + "/", "");

        copy = YamlConfiguration.loadConfiguration(file);
        Reader defConfigStream;

        try {
            defConfigStream = new InputStreamReader(plugin.getResource(filter), StandardCharsets.UTF_8);

            if (defConfigStream == null) {
                plugin.getLogger().log(Level.SEVERE, "Resource not found: " + filter);
                return;
            }

            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            copy.setDefaults(defConfig);
            defConfigStream.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean isExist(String path) {

        if (super.contains(path)) return true;
        if (copy == null) this.setCopyResource();

        plugin.getLogger().log(Level.INFO, "path no found: " + path + " in the file " + file.toString());
        return false;
    }


    public String getString(final String path, final boolean color) {
        if (!isExist(path)) return copy.getString(path);

        String value = getString(path) == null ? "path no found: " + path : getString(path);
        return color ? org.bukkit.ChatColor.translateAlternateColorCodes('&', value) : value;
    }

    /* Primitive object get and set */
    @Override
    public int getInt(String key) {

        if (!isExist(key)) return copy.getInt(key);
        return super.getInt(key);
    }

    @Override
    public String getString(String key) {
        if (!isExist(key)) return copy.getString(key);
        return super.getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        if (!isExist(key)) return copy.getBoolean(key);
        return super.getBoolean(key);
    }

    public @NotNull List<String> getStringList(String key) {
        if (!isExist(key)) return copy.getStringList(key);
        return super.getStringList(key);
    }

    public boolean exists() {
        return this.file.exists();
    }

    public void save() {
        try {
            this.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "There was an error while saving the " + file.getName() + " configuration!", e);
        }
    }

    public void reload() {
        try {
            this.copy = null;
            this.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            plugin.getLogger().log(Level.SEVERE, "There was an error while creating the " + file.getName() + " configuration file!", exception);
        }
    }

    public void delete() {
        if (this.file.exists()) {
            this.file.delete();
        }
    }

}