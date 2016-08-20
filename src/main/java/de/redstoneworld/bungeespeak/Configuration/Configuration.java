package de.redstoneworld.bungeespeak.Configuration;

import java.util.List;

import de.redstoneworld.bungeespeak.TsTarget;
import de.redstoneworld.bungeespeak.BungeeSpeak;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.event.EventPriority;


public enum Configuration {

	PREFIX("prefix", "&a[&6BungeeSpeak&a]&f "),

	MAIN_IP("main.TeamSpeakIp", "1.2.3.4"),
	MAIN_SERVERPORT("main.TeamSpeakPort", 9987),
	MAIN_QUERYPORT("main.QueryPort", 10011),
	MAIN_USERNAME("main.QueryUsername", "admin"),
	MAIN_PASSWORD("main.QueryPassword", "123456"),

	TS_NICKNAME("teamspeak.TeamspeakNickname", "Minecraft"),
	TS_CONSOLE_NAME("teamspeak.ConsoleName", "&4Server"),
	TS_CHANNEL_ID("teamspeak.ChannelID", 0),
	TS_CHANNEL_PASSWORD("teamspeak.ChannelPassword", ""),
	TS_ENABLE_SERVER_EVENTS("teamspeak.SendServerEventsToMinecraft", true,
			new String[] {"teamspeak.ListenToServerEvents"}),
	TS_ENABLE_SERVER_MESSAGES("teamspeak.SendServerBroadcastsToMinecraft", true,
			new String[] {"teamspeak.ListenToServerBroadcasts"}),
	TS_ENABLE_CHANNEL_EVENTS("teamspeak.SendChannelEventsToMinecraft", true,
			new String[] {"teamspeak.ListenToChannel"}),
	TS_ENABLE_CHANNEL_MESSAGES("teamspeak.SendChannelChatToMinecraft", true,
			new String[] {"teamspeak.ListenToChannelChat"}),
	TS_ENABLE_PRIVATE_MESSAGES("teamspeak.EnablePrivateMessaging", true,
			new String[] {"teamspeak.ListenToPrivateMessages"}),
	TS_ALLOW_LINKS("teamspeak.AllowLinksInMessages", true),
	TS_MESSAGES_TARGET("teamspeak.SendChatToTeamspeak", "channel"),
	TS_LIST_GROUPING("teamspeak.GroupClientListByServerGroup", false),
	TS_LOGGING("teamspeak.LogChatInConsole", true),
	TS_CHAT_LISTENER_PRIORITY("teamspeak.ChatListenerPriority", "MONITOR"),
	TS_DEBUGGING("teamspeak.Debug", false),

	TS_COMMANDS_ENABLED("teamspeak-commands.Enabled", false),
	TS_COMMANDS_PREFIX("teamspeak-commands.CommandPrefix", "!"),
	TS_COMMANDS_LOGGING("teamspeak-commands.LogTeamspeakCommands", true),
	TS_COMMANDS_MESSAGE_BUFFER("teamspeak-commands.MessageBufferDelay", 100),
	TS_COMMANDS_INTERNAL_LIST("teamspeak-commands.internal.ListCommandEnabled", true),
	TS_COMMANDS_INTERNAL_PM("teamspeak-commands.internal.PmCommandEnabled", true),

	PLUGINS_CHAT_RECIPIENTS_MUST_BE_EVERYONE("plugin-interaction.OnlyRelayChatAllPlayersWillReceive", false),

	PLUGINS_FACTIONS_PUBLIC_ONLY("plugin-interaction.Factions.public-only", false),

	PLUGINS_HEROCHAT_ENABLED("plugin-interaction.Herochat.enabled", false),
	PLUGINS_HEROCHAT_CHANNEL("plugin-interaction.Herochat.channel", "Global"),
	PLUGINS_HEROCHAT_RELAY_EVENTS("plugin-interaction.Herochat.SendTeamspeakEventsToChannel", false),

	PLUGINS_MCMMO_FILTER_PARTY_CHAT("plugin-interaction.mcMMO.FilterPartyChat", true),
	PLUGINS_MCMMO_FILTER_ADMIN_CHAT("plugin-interaction.mcMMO.FilterAdminChat", true);

	private final String path;
	private final String[] oldPaths;
	private final Object defValue;

	Configuration(String configPath, Object defaultValue) {
		this(configPath, defaultValue, null);
	}

	Configuration(String configPath, Object defaultValue, String[] oldConfigPaths) {
		path = configPath;
		oldPaths = oldConfigPaths;
		defValue = defaultValue;
	}

	public static void reload() {
		boolean changed = false;
		boolean moved = false;
		BungeeSpeak.getInstance().reloadConfig();
		FileConfiguration config = BungeeSpeak.getInstance().getConfig();
		config.setDefaults(new MemoryConfiguration()); // No Bukkit. No. Bad Bukkit.

		if (config.getKeys(false).isEmpty()) {
			BungeeSpeak.getInstance().saveResource("config.yml", false);
			BungeeSpeak.log().info("Default config file created!");
			BungeeSpeak.getInstance().reloadConfig();
			config = BungeeSpeak.getInstance().getConfig();
		}

		ValueIteration: for (Configuration value : Configuration.values()) {
			if (value.defValue == null) continue;
			Object val = config.get(value.path);

			if (val == null) {
				if (value.oldPaths != null) {
					for (String oldPath : value.oldPaths) {
						Object oldVal = config.get(oldPath);
						if (oldVal != null && oldVal.getClass().isInstance(value.defValue)) {
							config.set(oldPath, null);
							config.set(value.path, oldVal);

							if (Configuration.TS_DEBUGGING.getBoolean()) {
								BungeeSpeak.log().info("Moved \"" + oldPath + "\" to \"" + value.path + "\".");
							}

							changed = true;
							moved = true;
							continue ValueIteration;
						}
					}
				}

				value.setToDefault();
				BungeeSpeak.log().warning("Config value \"" + value.path + "\" was not set, changed it to \""
						+ String.valueOf(value.defValue) + "\".");
				changed = true;
			} else if (!val.getClass().isInstance(value.defValue)) {
				value.setToDefault();
				BungeeSpeak.log().warning("Config value \"" + value.path + "\" was not of type "
						+ value.defValue.getClass().getSimpleName() + ", changed it to \""
						+ String.valueOf(value.defValue) + "\".");
				changed = true;
			}
		}

		if (moved) removeEmptySections(config);
		if (changed) save();
	}

	private static void removeEmptySections(FileConfiguration fileConfig) {
		boolean removed = false;
		for (String key : fileConfig.getKeys(true)) {
			if (!fileConfig.isConfigurationSection(key)) continue;
			if (fileConfig.getConfigurationSection(key).getKeys(false).isEmpty()) {
				fileConfig.set(key, null);
				removed = true;
			}
		}
		if (removed) removeEmptySections(fileConfig);
	}

	public static void save() {
		BungeeSpeak.getInstance().saveConfig();
	}

	public static FileConfiguration getConfig() {
		return BungeeSpeak.getInstance().getConfig();
	}

	public String getConfigPath() {
		return path;
	}

	public Object getDefaultValue() {
		return defValue;
	}

	public Object get() {
		return BungeeSpeak.getInstance().getConfig().get(path, defValue);
	}

	public String getString() {
		return BungeeSpeak.getInstance().getConfig().getString(path, (String) defValue);
	}

	public int getInt() {
		return BungeeSpeak.getInstance().getConfig().getInt(path, (Integer) defValue);
	}

	public boolean getBoolean() {
		return BungeeSpeak.getInstance().getConfig().getBoolean(path, (Boolean) defValue);
	}

	public double getDouble() {
		return BungeeSpeak.getInstance().getConfig().getDouble(path, (Double) defValue);
	}

	public long getLong() {
		return BungeeSpeak.getInstance().getConfig().getLong(path, (Long) defValue);
	}

	public List<?> getList() {
		return BungeeSpeak.getInstance().getConfig().getList(path, (List<?>) defValue);
	}

	public ChatColor getColor() {
		return BungeeSpeak.getInstance().getConfig().getColor(path, (Color) defValue);
	}

	public TsTarget getTeamspeakTarget() {
		TsTarget tsTarget = TsTarget.getFromString(getString());
		if (tsTarget == null) {
			BungeeSpeak.log().warning("Config value \"" + path
					+ "\" did not match a valid Teamspeak target. Not sending messages to TeamSpeak.");
			return TsTarget.NONE;
		} else {
			return tsTarget;
		}
	}

	public void set(Object value) {
		BungeeSpeak.getInstance().getConfig().set(path, value);
	}

	public void setToDefault() {
		BungeeSpeak.getInstance().getConfig().set(path, defValue);
	}
}
