package de.crafttogether.velocityspeak.Configuration;

import com.velocitypowered.api.plugin.Plugin;
import de.crafttogether.velocityspeak.VelocitySpeak;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class YamlConfig {
    private Configuration defaultCfg = new Configuration();
    private Configuration cfg;
    private final static ConfigurationProvider ymlCfg = ConfigurationProvider.getProvider( YamlConfiguration.class );

    protected File configFile;

    private final VelocitySpeak plugin = VelocitySpeak.getInstance();
    
    /**
     * Read configuration into memory
     * @param configFilePath The path where the config's file should be saved in
     * @throws IOException When it could not create or load the file
     */
    public YamlConfig(String configFilePath) throws IOException {
        configFile = new File(configFilePath);
        InputStream defaultStream = plugin.getClass().getResourceAsStream("/" + configFile.getName()); // TODO: Check if work?
        if (defaultStream != null) {
            defaultCfg = ymlCfg.load(new InputStreamReader(defaultStream));
        }

        if (!configFile.exists()) {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            configFile.createNewFile();

            createDefaultConfig();
        } else {
            cfg = ymlCfg.load(configFile);
        }
    }

    /**
     * save configuration to disk
     */
    public void save() {
        try {
            ymlCfg.save(cfg, configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Unable to save configuration at " + configFile.getAbsolutePath());
            e.printStackTrace();
        }
    }
    
    public void createDefaultConfig() {
        cfg = defaultCfg;

        save();
    }    
    
    /**
     * deletes configuration file
     */
    public void removeConfig() {
        configFile.delete();
    }

    public Object get(String path, Object defValue) {
        return cfg.get(path, defValue);
    }

    public Collection<String> getKeys() {
        return cfg.getKeys();
    }

    public boolean getBoolean(String path) {
        return cfg.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def) {
        return cfg.getBoolean(path, def);
    }

    public double getDouble(String path) {
        return cfg.getDouble(path);
    }

    public double getDouble(String path, double defValue) {
        return cfg.getDouble(path, defValue);
    }

    public long getLong(String path) {
        return cfg.getLong(path);
    }

    public long getLong(String path, long defValue) {
        return cfg.getLong(path, defValue);
    }

    public int getInt(String path) {
        return cfg.getInt(path);
    }

    public int getInt(String path, int def) {
        return cfg.getInt(path, def);
    }

    public String getString(String path) {
        return cfg.getString(path);
    }

    public String getString(String path, String def) {
        return cfg.getString(path, def);
    }

    public List<?> getList(String path) {
        return cfg.getList(path);
    }

    public List<?> getList(String path, List<?> defValue) {
        return cfg.getList(path, defValue);
    }

    public List<String> getStringList(String path) {
        return cfg.getStringList(path);
    }

    public Configuration getSection(String path) {
        return cfg.getSection(path);
    }

    public Configuration getDefaults() {
        return defaultCfg;
    }

    public boolean isSet(String path) {
        return cfg.get(path) != null;
    }

    public void set(String path, Object value) {
        cfg.set(path, value);
    }

    public boolean isConfigurationSection(String key) {
        return cfg.get(key) instanceof Map;
    }
}