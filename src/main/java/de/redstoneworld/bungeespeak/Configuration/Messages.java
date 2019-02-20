package de.redstoneworld.bungeespeak.Configuration;

import java.io.File;
import java.io.IOException;

import de.redstoneworld.bungeespeak.BungeeSpeak;

public enum Messages {

	// Teamspeak events
	TS_EVENT_SERVER_JOIN("TeamspeakEvent.ServerJoin.ToMinecraftServer",
			"&e%client_nickname% &ahas joined TeamSpeak"),

	TS_EVENT_SERVER_QUIT("TeamspeakEvent.ServerQuit.ToMinecraftServer",
			"&e%client_nickname% &ahas left TeamSpeak"),

	TS_EVENT_CHANNEL_ENTER("TeamspeakEvent.ChannelEnter.ToMinecraftServer",
			"&e%client_nickname% &aentered the channel."),

	TS_EVENT_CHANNEL_LEAVE("TeamspeakEvent.ChannelLeave.ToMinecraftServer",
			"&e%client_nickname% &aleft the channel."),

	TS_EVENT_SERVER_MESSAGE("TeamspeakEvent.ServerMessage.ToMinecraftServer",
			"[&cTS&f] &e%client_nickname%&a: %msg%"),

	TS_EVENT_CHANNEL_MESSAGE("TeamspeakEvent.ChannelMessage.ToMinecraftServer",
			"&e%client_nickname%&f: %msg%"),

	TS_EVENT_PRIVATE_MESSAGE("TeamspeakEvent.PrivateMessage.ToMinecraftUser",
			"&e%client_nickname% &a-> &eMe&f: %msg%"),

	TS_EVENT_PRIVATE_MESSAGE_NO_CONVERSATION("TeamspeakEvent.PrivateMessage.Errors.NotInConversation.ToTeamspeakUser",
			"&4You're currently not in a private message conversation."),

	TS_EVENT_PRIVATE_MESSAGE_RECIPIENT_OFFLINE("TeamspeakEvent.PrivateMessage.Errors.PmRecipientNotOnline.ToTeamspeakUser",
			"&4The user you're trying to send a message to is offline."),

	TS_EVENT_PRIVATE_MESSAGE_RECIPIENT_MUTED("TeamspeakEvent.PrivateMessage.Errors.PmRecipientMutedOrNoPermission.ToTeamspeakUser",
			"&4The user &l%player_displayname%&r&4 can't receive your message."),

	// Minecraft events
	MC_EVENT_CHAT("MinecraftEvent.PlayerChat.ToTeamspeakTarget",
			"&l%player_displayname%&r: %msg%"),

	MC_EVENT_LOGIN("MinecraftEvent.PlayerLogin.ToTeamspeakTarget",
			"&l%player_displayname%&r logged in."),

	MC_EVENT_LOGOUT("MinecraftEvent.PlayerLogout.ToTeamspeakTarget",
			"&l%player_displayname%&r logged out."),

	MC_EVENT_KICK("MinecraftEvent.PlayerKicked.ToTeamspeakTarget",
			"&l%player_displayname%&r was kicked from the server."),

	MC_EVENT_BAN("MinecraftEvent.PlayerBanned.ToTeamspeakTarget",
			"&l%player_displayname%&r was banned from the server."),

	// Minecraft commands
	MC_COMMAND_USAGE_TS("MinecraftCommand.Usage.Ts",
			"Type /ts for help."),
	
	MC_COMMAND_USAGE_TSA("MinecraftCommand.Usage.Tsa",
			"Type /tsa for help."),
	
	MC_COMMAND_HELP_USER("MinecraftCommand.Help.UserCommands.ToMinecraftUser",
			"&e%command% &a- %description%"),

	MC_COMMAND_HELP_USER_HEADER("MinecraftCommand.Help.UserCommands.Header",
			"&a[&6BungeeSpeak&a] &aHelp"),
    
    MC_COMMAND_HELP_USER_FOOTER("MinecraftCommand.Help.UserCommands.Footer",
            ""),

	MC_COMMAND_HELP_ADMIN("MinecraftCommand.Help.AdminCommands.ToMinecraftUser",
			"&e%command% &2- %description%"),

	MC_COMMAND_HELP_ADMIN_HEADER("MinecraftCommand.Help.AdminCommands.Header",
			"&a[&6BungeeSpeak&a] &2Admin Commands Help"),
    
    MC_COMMAND_HELP_ADMIN_FOOTER("MinecraftCommand.Help.AdminCommands.Footer",
            ""),

	MC_COMMAND_HELP_ADMIN_DESCRIPTION("MinecraftCommand.Help.AdminCommands.Description",
			"BungeeSpeak admin commands."),

	MC_COMMAND_HELP_ADMIN_COMMAND("MinecraftCommand.Help.AdminCommands.Command",
			"/ts admin &aor &e/tsa"),

	MC_COMMAND_LIST_DESCRIPTION("MinecraftCommand.List.Description",
			"Displays who's currently on TeamSpeak."),

	MC_COMMAND_LIST_SERVER("MinecraftCommand.List.Server.ToMinecraftUser",
			"&aCurrently online (&e%count%&a): &e%list%"),

	MC_COMMAND_LIST_CHANNEL("MinecraftCommand.List.Channel.ToMinecraftUser",
			"&aCurrently in the channel (&e%count%&a): &e%list%"),

	MC_COMMAND_MUTE("MinecraftCommand.Mute.Enable.ToMinecraftUser",
			"&aYou are now muted."),

	MC_COMMAND_UNMUTE("MinecraftCommand.Mute.Disable.ToMinecraftUser",
			"&aYou aren't muted anymore."),

	MC_COMMAND_MUTE_DESCRIPTION("MinecraftCommand.Mute.Description",
			"Mutes / unmutes BungeeSpeak for you."),

	MC_COMMAND_CHANNEL_CHANGE("MinecraftCommand.Set.ChannelChange.ToMinecraftServer",
			"&aYou are now talking in the TeamSpeak channel &6%channel%&a."),

	MC_COMMAND_BROADCAST_MC("MinecraftCommand.Broadcast.ToMinecraftServer",
			"&e%player_displayname% &a-> &f[&cTS&f]&f: %msg%"),

	MC_COMMAND_BROADCAST_TS("MinecraftCommand.Broadcast.ToTeamspeakServer",
			"&4&l%msg%"),

	MC_COMMAND_BROADCAST_USAGE("MinecraftCommand.Broadcast.Usage",
			"/ts broadcast message"),

	MC_COMMAND_BROADCAST_DESCRIPTION("MinecraftCommand.Broadcast.Description",
			"Broadcast a global TS message."),

	MC_COMMAND_CHAT_MC("MinecraftCommand.Chat.ToMinecraftServer",
			"&e%player_displayname% &a-> &eTS&f: %msg%"),

	MC_COMMAND_CHAT_TS("MinecraftCommand.Chat.ToTeamspeakChannel",
			"&l[%player_displayname%&r&l] &r%msg%"),

	MC_COMMAND_CHAT_USAGE("MinecraftCommand.Chat.Usage",
			"/ts chat message"),

	MC_COMMAND_CHAT_DESCRIPTION("MinecraftCommand.Chat.Description",
			"Displays a message in the TS channel."),

	MC_COMMAND_PM_MC("MinecraftCommand.Pm.ToMinecraftUser",
			"&eMe &a-> &e%target%&f: %msg%"),

	MC_COMMAND_PM_TS("MinecraftCommand.Pm.ToTeamspeakUser",
			"&l[%player_displayname%&r&l] &r%msg%"),

	MC_COMMAND_PM_USAGE("MinecraftCommand.Pm.Usage",
			"/ts pm target message"),

	MC_COMMAND_PM_DESCRIPTION("MinecraftCommand.Pm.Description",
			"Sends a message to a certain client."),

	MC_COMMAND_REPLY_NO_RECIPIENT("MinecraftCommand.Reply.Errors.NoRecipient.ToMinecraftUser",
			"&4Nobody has sent you a PM yet."),

	MC_COMMAND_REPLY_USAGE("MinecraftCommand.Reply.Usage",
			"/ts r(eply) message"),

	MC_COMMAND_REPLY_DESCRIPTION("MinecraftCommand.Reply.Description",
			"Replies to a PM."),

	MC_COMMAND_POKE_MC("MinecraftCommand.Poke.ToMinecraftUser",
			"&eYou &apoked &e%target%&f: %msg%"),

	MC_COMMAND_POKE_TS("MinecraftCommand.Poke.ToTeamspeakUser",
			"&l[%player_displayname%&r&l] &r%msg%"),

	MC_COMMAND_POKE_USAGE("MinecraftCommand.Poke.Usage",
			"/ts poke target message"),

	MC_COMMAND_POKE_DESCRIPTION("MinecraftCommand.Poke.Description",
			"Pokes a client on Teamspeak."),

	MC_COMMAND_INFO_DESCRIPTION("MinecraftCommand.Info.Description",
			"Information about the TS server."),

	MC_COMMAND_INFO_HEADER_PORT("MinecraftCommand.Info.Header.Port",
			"&aTeamspeak IP: &e%address%"),

	MC_COMMAND_INFO_HEADER_VIRTUAL("MinecraftCommand.Info.Header.Virtual",
			"&aTeamspeak IP: &e%address%&a, Virtual Server ID: &e%id%"),

	MC_COMMAND_INFO_TEXT("MinecraftCommand.Info.Text",
			"&aClients online: &e%count%"),

	MC_COMMAND_BAN_MC("MinecraftCommand.Ban.ToMinecraftServer",
			"&e%player_displayname% &abanned &e%target% &afor &e%msg%&a."),

	MC_COMMAND_BAN_TS("MinecraftCommand.Ban.ToTeamspeakUser",
			"[%player_displayname%] banned you from the server for %msg%."),

	MC_COMMAND_BAN_USAGE("MinecraftCommand.Ban.Usage",
			"/ts ban client (message)"),

	MC_COMMAND_BAN_DESCRIPTION("MinecraftCommand.Ban.Description",
			"Bans a client."),

	MC_COMMAND_KICK_MC("MinecraftCommand.Kick.ToMinecraftServer",
			"&e%player_displayname% &akicked &e%target% &afrom the server for &e%msg%&a."),

	MC_COMMAND_KICK_TS("MinecraftCommand.Kick.ToTeamspeakUser",
			"[%player_displayname%] kicked you from the server for %msg%."),

	MC_COMMAND_KICK_USAGE("MinecraftCommand.Kick.Usage",
			"/ts kick client (message)"),

	MC_COMMAND_KICK_DESCRIPTION("MinecraftCommand.Kick.Description",
			"Kicks from the TS server."),

	MC_COMMAND_CHANNEL_KICK_MC("MinecraftCommand.ChannelKick.ToMinecraftServer",
			"&e%player_displayname% &akicked &e%target% &afrom the channel for &e%msg%&a."),

	MC_COMMAND_CHANNEL_KICK_TS("MinecraftCommand.ChannelKick.ToTeamspeakUser",
			"[%player_displayname%] kicked you from the server for %msg%."),

	MC_COMMAND_CHANNEL_KICK_NOT_IN_CHANNEL("MinecraftCommand.ChannelKick.Errors.ClientNotInChannel.ToMinecraftUser",
			"&4The client &r&e%target%&r&4 is not in the channel!"),

	MC_COMMAND_CHANNEL_KICK_USAGE("MinecraftCommand.ChannelKick.Usage",
			"/ts channelkick client (message)"),

	MC_COMMAND_CHANNEL_KICK_DESCRIPTION("MinecraftCommand.ChannelKick.Description",
			"Kicks from the channel and moves the client to the default channel."),

	MC_COMMAND_SET_DESCRIPTION("MinecraftCommand.Set.Description",
			"Change BungeeSpeak's config."),

	MC_COMMAND_STATUS_DESCRIPTION("MinecraftCommand.Status.Description",
			"Shows some info about BungeeSpeak."),

	MC_COMMAND_RELOAD_DESCRIPTION("MinecraftCommand.Reload.Description",
			"Reloads the config and the query."),

	MC_COMMAND_DEFAULT_REASON("MinecraftCommand.KickBanDefaultReason",
			"-"),

	MC_COMMAND_ERROR_DISCONNECTED("MinecraftCommand.Errors.NotConnected.ToMinecraftUser",
			"&4Can't communicate with the TeamSpeak server."),

	MC_COMMAND_ERROR_NO_PLAYER_FOUND("MinecraftCommand.Errors.NoPlayerFound.ToMinecraftUser",
			"&4There is no player by the name of &e%input%&4."),

	MC_COMMAND_ERROR_MULTIPLE_PLAYERS_FOUND("MinecraftCommand.Errors.MultiplePlayersFound.ToMinecraftUser",
			"&4There is more than one player matching &e%input%&4."),

	MC_COMMAND_ERROR_MESSAGE_TOO_LONG("MinecraftCommand.Errors.MessageTooLong.ToMinecraftUser",
			"&4The message is too long! (> 100 characters)"),

	MC_COMMAND_ERROR_MESSAGE_TOO_FEW_ARGS("MinecraftCommand.Errors.TooFewArguments.ToMinecraftUser",
			"&aToo few arguments!"),

	MC_COMMAND_ERROR_MESSAGE_USAGE("MinecraftCommand.Errors.UsageInfo.ToMinecraftUser",
			"&aUsage: &e%usage%"),

	// Teamspeak commands
	TS_COMMAND_LIST("TeamspeakCommand.List.ToTeamspeakUser",
			"Currently online (%count%): %list%"),

	TS_COMMAND_PM("TeamspeakCommand.Pm.ToTeamspeakUser",
			"&4Me &r-> &4%player_displayname%&r: %msg%"),

	TS_COMMAND_PM_CONVERSATION_STARTED("TeamspeakCommand.Pm.ConversationStarted.ToTeamspeakUser",
			"Started conversation with player %player_displayname%. You can now chat directly without typing !pm"),

	TS_COMMAND_PM_NO_PLAYER_BY_THIS_NAME("TeamspeakCommand.Pm.Errors.NoPlayerByThisName.ToTeamspeakUser",
			"&4No Minecraft player by the name of &e%input%&4."),

	TS_COMMAND_PM_RECIPIENT_MUTED("TeamspeakCommand.Pm.Errors.RecipientMutedOrNoPermission.ToTeamspeakUser",
			"&4The user &l%player_displayname%&r&4 can't receive your message."),

	TS_COMMAND_SENDER_NAME("TeamspeakCommand.CommandSenderName", "&a[&6TS&a] &e%client_nickname%&r"),

	TS_COMMAND_NOT_WHITELISTED("TeamspeakCommand.Errors.PluginNotWhitelisted.ToTeamspeakUser",
			"You are not allowed to run commands of that plugin."),

	TS_COMMAND_BLACKLISTED("TeamspeakCommand.Errors.CommandBlacklisted.ToTeamspeakUser",
			"The command you are trying to run is blacklisted.");

	private static YamlConfig config;

	private final String path;
	private final String defValue;

	Messages(String configPath, String defaultValue) {
		path = configPath;
		defValue = defaultValue;
	}

	public static void reload() throws IOException {
		BungeeSpeak.log().info("Loading locale!");
		config = new YamlConfig(BungeeSpeak.getInstance(), BungeeSpeak.getInstance().getDataFolder() + File.separator + "locale.yml");
	}


	public static void save() {
		if (config == null) {
			try {
				config = new YamlConfig(BungeeSpeak.getInstance(), BungeeSpeak.getInstance().getDataFolder() + File.separator + "locale.yml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			config.save();
		}
	}

	public static YamlConfig getConfig() {
		return config;
	}

	public String getConfigPath() {
		return path;
	}

	public String getDefaultValue() {
		return defValue;
	}

	public String get() {
		if (config == null) {
			throw new IllegalAccessError("You need to load the configuration first!");
		}
		return config.getString(path, defValue);
	}

	public void set(String value) {
		config.set(path, value);
	}

	public void setToDefault() {
		config.set(path, defValue);
	}
}
