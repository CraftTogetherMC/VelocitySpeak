package de.redstoneworld.bungeespeak;

import java.util.Date;
import java.util.logging.Logger;

import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import de.redstoneworld.bungeespeak.Configuration.Configuration;

public class QueryConnector implements Runnable {

	private BungeeSpeak plugin;
	private TS3Query query;
	private Logger logger;

	public QueryConnector() {
		this.plugin = BungeeSpeak.getInstance();
		query = BungeeSpeak.getQuery();
		logger = plugin.getLogger();
	}

	public void run() {
		plugin.setStartedTime(new Date());
		if (query.isConnected()) {
			query.getApi().unregisterAllEvents();
		}

		try {
			query.connect();
		} catch (TS3CommandFailedException ex) {
			if (plugin.getStoppedTime() == null) {
				logger.severe("Could not connect to the TS3 server.");
				logger.severe("Make sure that the IP and the QueryPort are correct!");
				logger.severe("You might also be (flood) banned from the server. Check the query whitelist!");
				logger.severe("(" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage() + ")");
			}

			plugin.setStoppedTime(new Date());
			return;
		}
		try {
			query.getApi().login(Configuration.MAIN_USERNAME.getString(), Configuration.MAIN_PASSWORD.getString());
		} catch (TS3CommandFailedException ex) {
			if (plugin.getStoppedTime() == null) {
				logger.severe("Could not login to the Server Query.");
				logger.severe("Make sure that \"QueryUsername\" and \"QueryPassword\" are correct.");
				logger.severe("(" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage() + ")");
			}

			plugin.setStoppedTime(new Date());
			query.exit();
			return;
		}
		if (Configuration.MAIN_SERVERPORT.getInt() > 0) {
			try {
				query.getApi().selectVirtualServerByPort(Configuration.MAIN_SERVERPORT.getInt());
			} catch (TS3CommandFailedException ex) {
				if (plugin.getStoppedTime() == null) {
					logger.severe("Could not select the virtual server.");
					logger.severe("Make sure TeamSpeakPort is PortNumber OR -VirtualServerId");
					logger.severe("(" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage() + ")");
				}

				plugin.setStoppedTime(new Date());
				query.exit();
				return;
			}
		} else {
			try {
				query.getApi().selectVirtualServerById(-Configuration.MAIN_SERVERPORT.getInt());
			} catch (TS3CommandFailedException ex) {
				if (plugin.getStoppedTime() == null) {
					logger.severe("Could not select the virtual server.");
					logger.severe("Make sure TeamSpeakPort is PortNumber OR -VirtualServerId");
					logger.severe("(" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage() + ")");
				}

				plugin.setStoppedTime(new Date());
				query.exit();
				return;
			}
		}
		try {
			query.getApi().setNickname(Configuration.TS_NICKNAME.getString());
		} catch (TS3CommandFailedException ex) {
			logger.warning("Could not set the nickname on Teamspeak.");
			logger.warning("Make sure that the name isn't occupied.");
			logger.severe("(" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage() + ")");
		}

		query.getApi().addTS3Listeners(plugin.getTSActionListener());

		if (Configuration.TS_ENABLE_SERVER_EVENTS.getBoolean()) {
			query.getApi().registerEvent(TS3EventType.SERVER);
		}

		if (Configuration.TS_ENABLE_SERVER_MESSAGES.getBoolean()) {
			query.getApi().registerEvent(TS3EventType.TEXT_SERVER);
		}

		final int cid = Configuration.TS_CHANNEL_ID.getInt();
		final boolean channelEvents = Configuration.TS_ENABLE_CHANNEL_EVENTS.getBoolean();
		final boolean channelMessages = Configuration.TS_ENABLE_CHANNEL_MESSAGES.getBoolean();
		if (cid != 0 && (channelEvents || channelMessages)) {
			try {
				query.getApi().moveQuery(cid, Configuration.TS_CHANNEL_PASSWORD.getString());
			} catch (TS3CommandFailedException ex) {
				logger.severe("Could not move the QueryClient into the channel.");
				logger.severe("Ensure that the ChannelID is correct and the password is set if required.");
				logger.severe("(" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage() + ")");
			}
		}

		BungeeSpeak.updateQueryInfo();

		if (channelEvents) {
			query.getApi().registerEvent(TS3EventType.CHANNEL, BungeeSpeak.getQueryInfo().getChannelId());
		}
		if (channelMessages) {
			query.getApi().registerEvent(TS3EventType.TEXT_CHANNEL, BungeeSpeak.getQueryInfo().getChannelId());
		}
		if (Configuration.TS_ENABLE_PRIVATE_MESSAGES.getBoolean()) {
			query.getApi().registerEvent(TS3EventType.TEXT_PRIVATE);
		}

		BungeeSpeak.getInstance().resetLists();
		plugin.setStoppedTime(null);
		plugin.setStartedTime(null);
		plugin.setStartedTime(new Date());
		logger.info("Connected with SID = " + BungeeSpeak.getQueryInfo().getVirtualServerId() + ", CID = "
				+ BungeeSpeak.getQueryInfo().getChannelId() + ", CLID = " + BungeeSpeak.getQueryInfo().getId());
	}
}
