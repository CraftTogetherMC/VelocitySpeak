package de.crafttogether.velocityspeak;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.Listeners.ChatListener;
import de.crafttogether.velocityspeak.Listeners.PlayerListener;
import de.crafttogether.velocityspeak.TeamspeakCommands.PermissionsHelper;
import de.crafttogether.velocityspeak.TeamspeakCommands.TeamspeakCommandExecutor;
import de.crafttogether.velocityspeak.teamspeakEvent.TeamspeakListener;
import de.crafttogether.velocityspeak.util.ChatColor;

@Plugin(
		id = "velocityspeak",
		name = "VelocitySpeak",
		version = "0.80-SNAPSHOT",
		url = "https://github.com/CraftTogetherMC/VelocitySpeak",
		description = "This plugin will display Teamspeak 3 server joins, quits and messages in Minecraft and allows you to chat with your friends using the /ts commands!",
		authors = { "Deathmarine", "Firedroide", "but2002", "greycap", "J0schlZ"}
)

public class VelocitySpeak {
	private static int queryId;
	private static ServerQueryInfo queryInfo;
	private boolean enabled = false;

	private static final int KEEP_ALIVE_DELAY = 60;

	private static VelocitySpeak instance;
	private static TeamspeakCommandExecutor tsCommand;
	private static PermissionsHelper permissionsHelper;
	private static ClientList clients;
	private static ChannelList channels;
	private static TS3Query query;

	private static List<String> muted;
	private static HashMap<Integer, String> pmRecipients;
	private static HashMap<String, Integer> pmSenders;

	private QueryConnector qc;
	private TS3Listener ts;
	private TeamspeakKeepAlive tsKeepAlive;
	private TsCommandExecutor mcTsCommand;
	private TsaCommandExecutor mcTsaCommand;
	private PlayerListener playerListener;
	private ChatListener chatListener = new ChatListener(ChatListener.Priority.NORMAL);

	private ProxyServer proxy;
	private Logger logger;
	private Path dataDirectory;

	private Date started, stopped, laststarted, laststopped;

	public static VelocitySpeak getInstance() {
		return instance;
	}
	public ProxyServer getProxy() { return this.proxy; };
	public Logger getLogger() {
		return this.logger;
	}
	public Path getDataDirectory() {
		return this.dataDirectory;
	}

	@Inject
	public VelocitySpeak(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
		this.proxy = proxy;
		this.logger = logger;
		this.dataDirectory = dataDirectory;
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		instance = this;
		logger = this.getLogger();
		muted = new ArrayList<String>();
		pmRecipients = new HashMap<Integer, String>();
		pmSenders = new HashMap<String, Integer>();

		clients = new ClientList();
		channels = new ChannelList();
		permissionsHelper = new PermissionsHelper();

		if (!reload()) {
			onDisable();
		}

		ts = new TeamspeakListener();
		tsKeepAlive = new TeamspeakKeepAlive(this);

		proxy.getScheduler()
				.buildTask(this, tsKeepAlive)
				.delay(KEEP_ALIVE_DELAY / 2, TimeUnit.SECONDS)
				.repeat(KEEP_ALIVE_DELAY, TimeUnit.SECONDS)
				.schedule();

		mcTsCommand = new TsCommandExecutor();
		mcTsaCommand = new TsaCommandExecutor();
		playerListener = new PlayerListener();

		getProxy().getEventManager().register(this, chatListener);
		getProxy().getEventManager().register(this, playerListener);
		getProxy().getEventManager().register(this, mcTsCommand);
		getProxy().getEventManager().register(this, mcTsaCommand);

		enabled = true;
		logger.info("enabled.");
	}

	public void onDisable() {
		enabled = false;
		query.exit();

		for (ScheduledTask task : this.getProxy().getScheduler().tasksByPlugin(this)) {
			task.cancel();
		}

		logger.info("disabled.");
	}

	@Deprecated
	public String toString() {
		return ChatColor.translateAlternateColorCodes('&', Configuration.PREFIX.getString());
	}


	public static String getFullName() {
		return instance.toString();
	}

	public static Logger log() {
		return instance.logger;
	}

	public static TS3Query getQuery() {
		return query;
	}

	public static ServerQueryInfo getQueryInfo() {
		return queryInfo;
	}

	public static TeamspeakCommandExecutor getTeamspeakCommandExecutor() {
		return tsCommand;
	}

	public TsaCommandExecutor getTeamspeakAdminCommand() {
		return mcTsaCommand;
	}

	public static PermissionsHelper getPermissionsHelper() {
		return permissionsHelper;
	}

	public static List<String> getMutedList() {
		return muted;
	}

	public static boolean getMuted(Player player) {
		return muted.contains(player.getUsername());
	}

	public static void setMuted(Player player, boolean mute) {
		if (mute && !muted.contains(player.getUsername())) {
			muted.add(player.getUsername());
		} else if (!mute && muted.contains(player.getUsername())) {
			muted.remove(player.getUsername());
		}
	}

	public ChatListener getChatListener() {
		return chatListener;
	}

	public PlayerListener getPlayerListener() {
		return playerListener;
	}

	public static ClientList getClientList() {
		return clients;
	}

	public static ChannelList getChannelList() {
		return channels;
	}

	public void resetLists() {
		if (isFloodBanned()) return;
		clients.updateAll();
		if (isFloodBanned()) return;
		channels.updateAll();
		if (isFloodBanned()) return;
		if (Configuration.TS_COMMANDS_ENABLED.getBoolean() || Configuration.TS_LIST_GROUPING.getBoolean()) {
			permissionsHelper.run();
		}
	}

	private boolean isFloodBanned() {
		/*
		if (query.getLastErrorID() == 3331) {
			logger.severe("You were flood banned. You need to add the Minecraft server IP to the TeamSpeak query whitelist!");
			enabled = false;
			return true;
		}
		*/
		return false;
	}

	public static void registerRecipient(String player, int clid) {
		pmRecipients.put(clid, player);
		pmSenders.put(player, clid);
	}

	public String getRecipient(int clid) {
		return pmRecipients.get(clid);
	}

	public Integer getSender(String player) {
		return pmSenders.get(player);
	}

	public QueryConnector getQueryConnector() {
		return qc;
	}

	public TS3Listener getTSActionListener() {
		return ts;
	}

	public Date getStartedTime() {
		return started;
	}

	public Date getStoppedTime() {
		return stopped;
	}

	public Date getLastStartedTime() {
		return laststarted;
	}

	public Date getLastStoppedTime() {
		return laststopped;
	}

	public void setStartedTime(Date d) {
		if (d != null && started == null) {
			started = d;
			laststarted = null;
		} else if (d != null) {
			laststarted = d;
		} else {
			started = null;
			laststarted = null;
		}
	}

	public void setStoppedTime(Date d) {
		if (d != null && stopped == null) {
			stopped = d;
		} else if (d != null) {
			laststopped = d;
		} else {
			stopped = null;
			laststopped = null;
		}
	}

	public boolean reload() {
		try {
			if (query != null) {
				query.exit();
			}

			setStoppedTime(null);
			setStartedTime(null);

			try {
				Configuration.reload();
			} catch (IOException e) {
				getLogger().severe("Unable to load configuration! " + this.getClass().getAnnotation(Plugin.class).name() + " will not be enabled!");
				e.printStackTrace();
				return false;
			}
			try {
				Messages.reload();
			} catch (IOException e) {
				e.printStackTrace();
			}

			chatListener.setPriority(Configuration.TS_CHAT_LISTENER_PRIORITY.getPriority());

			tsCommand = new TeamspeakCommandExecutor();

			query = new TS3Query(new TS3Config()
					.setHost(Configuration.MAIN_IP.getString())
					.setQueryPort(Configuration.MAIN_QUERYPORT.getInt())
					.setFloodRate(TS3Query.FloodRate.custom(Configuration.TS_FLOODRATE.getInt()))
					.setEnableCommunicationsLogging(Configuration.TS_DEBUGGING.getBoolean())
			);

			qc = new QueryConnector();
			getProxy().getScheduler().buildTask(this, qc).schedule();

			this.getLogger().info("reloaded.");
			return true;
		} catch (Exception e) {
			this.getLogger().info("was unable to reload, an error happened.");
			e.printStackTrace();
			return false;
		}
	}

	public static void updateQueryInfo() {
		queryInfo = VelocitySpeak.getQuery().getApi().whoAmI();
	}

	public boolean isEnabled() {
		return enabled;
	}
}
