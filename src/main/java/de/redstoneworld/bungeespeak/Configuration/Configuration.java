package de.redstoneworld.bungeespeak.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.redstoneworld.bungeespeak.Listeners.ChatListener;
import de.redstoneworld.bungeespeak.TsTarget;
import de.redstoneworld.bungeespeak.BungeeSpeak;


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
	TS_ENABLE_SERVER_EVENTS("teamspeak.SendServerEventsToMinecraft", true),
	TS_ENABLE_SERVER_MESSAGES("teamspeak.SendServerBroadcastsToMinecraft", true),
	TS_ENABLE_CHANNEL_EVENTS("teamspeak.SendChannelEventsToMinecraft", true),
	TS_ENABLE_CHANNEL_MESSAGES("teamspeak.SendChannelChatToMinecraft", true),
	TS_ENABLE_PRIVATE_MESSAGES("teamspeak.EnablePrivateMessaging", true),
	TS_ALLOW_LINKS("teamspeak.AllowLinksInMessages", true),
	TS_MESSAGES_TARGET("teamspeak.SendChatToTeamspeak", "channel"),
	TS_LIST_GROUPING("teamspeak.GroupClientListByServerGroup", false),
	TS_LOGGING("teamspeak.LogChatInConsole", true),
	TS_CHAT_LISTENER_PRIORITY("teamspeak.ChatListenerPriority", "HIGHEST"),
	TS_DEBUGGING("teamspeak.Debug", false),

	TS_COMMANDS_ENABLED("teamspeak-commands.Enabled", false),
	TS_COMMANDS_PREFIX("teamspeak-commands.CommandPrefix", "!"),
	TS_COMMANDS_LOGGING("teamspeak-commands.LogTeamspeakCommands", true),
	TS_COMMANDS_MESSAGE_BUFFER("teamspeak-commands.MessageBufferDelay", 100),
	TS_COMMANDS_INTERNAL_LIST("teamspeak-commands.internal.ListCommandEnabled", true),
	TS_COMMANDS_INTERNAL_PM("teamspeak-commands.internal.PmCommandEnabled", true),

	MC_COMMANDS_CLIENTLIST_FILTER_RULES("minecraft-commands.filteredclientlist.FilterRules", Collections.singletonList("(?i)(.*)bot$")),
	MC_COMMANDS_CLIENTLIST_FILTER_INFO("minecraft-commands.filteredclientlist.FilterInfo", true),
	MC_COMMANDS_CLIENTLIST_FILTER_LIST("minecraft-commands.filteredclientlist.FilterList", false),

	PLUGINS_CHAT_RECIPIENTS_MUST_BE_EVERYONE("plugin-interaction.OnlyRelayChatAllPlayersWillReceive", false);

	private static YamlConfig config;

	private final String path;
	private final Object defValue;

	Configuration(String configPath, Object defaultValue) {
		path = configPath;
		defValue = defaultValue;
	}

	public static void reload() throws IOException {
		BungeeSpeak.log().info("Loading config!");
		config = new YamlConfig(BungeeSpeak.getInstance(), BungeeSpeak.getInstance().getDataFolder() + File.separator + "config.yml");
	}


	public static void save() {
		config.save();
	}

	public static YamlConfig getConfig() {
		return config;
	}

	public String getConfigPath() {
		return path;
	}

	public Object getDefaultValue() {
		return defValue;
	}

	public Object get() {
		return config.get(path, defValue);
	}

	public String getString() {
		return config.getString(path, (String) defValue);
	}

	public List<String> getStringList() {
		return config.getStringList(path);
	}

	public int getInt() {
		return config.getInt(path, (Integer) defValue);
	}

	public boolean getBoolean() {
		return config.getBoolean(path, (Boolean) defValue);
	}

	public double getDouble() {
		return config.getDouble(path, (Double) defValue);
	}

	public long getLong() {
		return config.getLong(path, (Long) defValue);
	}

	public ChatListener.Priority getPriority() {
		ChatListener.Priority p;
		try {
			p = ChatListener.Priority.valueOf(getString().toUpperCase());
			return p;
		} catch (IllegalArgumentException e) {
			BungeeSpeak.log().warning("Config value \"" + path + "\" did not match LOWEST, LOW, NORMAL, HIGH or HIGHEST!");
		}
		return ChatListener.Priority.NORMAL;
	}

	public List<?> getList() {
		return config.getList(path, (List<?>) defValue);
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
		config.set(path, value);
	}

	public void setToDefault() {
		config.set(path, defValue);
	}
}
