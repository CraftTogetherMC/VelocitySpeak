package de.redstoneworld.bungeespeak.Commands.Properties;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.util.MessageUtil;
import de.redstoneworld.bungeespeak.util.Replacer;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class SetProperty {

	protected void send(CommandSender sender, Level level, String msg) {
		String m = msg;
		if (sender instanceof ProxiedPlayer) {
			m = m.replaceAll("((&|$)([a-fk-orA-FK-OR0-9]))", "\u00A7$3");
			sender.sendMessage(BungeeSpeak.getFullName() + m);
		} else {
			m = m.replaceAll("((&|$|\u00A7)([a-fk-orA-FK-OR0-9]))", "");
			BungeeSpeak.log().log(level, m);
		}
	}

	protected void reloadListener() {
		BungeeSpeak.getQuery().getApi().unregisterAllEvents();

		if (Configuration.TS_ENABLE_SERVER_EVENTS.getBoolean()) {
			BungeeSpeak.getQuery().getApi().registerEvent(TS3EventType.SERVER);
		}
		if (Configuration.TS_ENABLE_SERVER_MESSAGES.getBoolean()) {
			BungeeSpeak.getQuery().getApi().registerEvent(TS3EventType.TEXT_SERVER);
		}
		if (Configuration.TS_ENABLE_CHANNEL_EVENTS.getBoolean()) {
			BungeeSpeak.getQuery().getApi().registerEvent(TS3EventType.CHANNEL,
					BungeeSpeak.getQueryInfo().getChannelId());
		}
		if (Configuration.TS_ENABLE_CHANNEL_MESSAGES.getBoolean()) {
			BungeeSpeak.getQuery().getApi().registerEvent(TS3EventType.TEXT_CHANNEL,
					BungeeSpeak.getQueryInfo().getChannelId());
		}
		if (Configuration.TS_ENABLE_PRIVATE_MESSAGES.getBoolean()) {
			BungeeSpeak.getQuery().getApi().registerEvent(TS3EventType.TEXT_PRIVATE);
		}
	}

	protected void broadcastMessage(String mcMsg, CommandSender sender) {
		if (mcMsg == null || mcMsg.isEmpty()) return;
		for (ProxiedPlayer pl : BungeeSpeak.getInstance().getProxy().getPlayers()) {
			if (!BungeeSpeak.getMuted(pl)) {
				pl.sendMessage(MessageUtil.toMinecraft(mcMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean()));
			}
		}
		if (!(sender instanceof ProxiedPlayer) || (Configuration.TS_LOGGING.getBoolean())) {
			BungeeSpeak.log().info(MessageUtil.toMinecraft(mcMsg, false, Configuration.TS_ALLOW_LINKS.getBoolean()));
		}
	}

	protected void sendChannelChangeMessage(CommandSender sender) {
		String mcMsg = Messages.MC_COMMAND_CHANNEL_CHANGE.get();
		int clientId = BungeeSpeak.getQueryInfo().getChannelId();
		Map<String, String> info = BungeeSpeak.getQuery().getApi().getChannelInfo(clientId).getMap();

		mcMsg = new Replacer().addSender(sender).addChannel(info).replace(mcMsg);
		broadcastMessage(mcMsg, sender);
	}

	protected void connectChannel(CommandSender sender) {
		int cid = BungeeSpeak.getQueryInfo().getChannelId();
		String pw = Configuration.TS_CHANNEL_PASSWORD.getString();
		try {
			BungeeSpeak.getQuery().getApi().moveQuery(cid, pw);
		} catch (TS3CommandFailedException ex) {
			send(sender, Level.WARNING, "&4The channel ID could not be set.");
			send(sender, Level.WARNING, "&4Ensure that the ChannelID is really assigned to a valid channel.");
			send(sender, Level.WARNING, "&4" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage());
			return;
		}
		sendChannelChangeMessage(sender);
	}

	public String getName() {
		String property = getProperty().getConfigPath();
		return property.substring(property.lastIndexOf(".") + 1);
	}

	public abstract Configuration getProperty();

	public abstract String getAllowedInput();

	public abstract String getDescription();

	public abstract boolean execute(CommandSender sender, String arg);

	public abstract List<String> onTabComplete(CommandSender sender, String[] args);
}
