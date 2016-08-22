package de.redstoneworld.bungeespeak.Commands.Properties;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.util.MessageUtil;
import de.redstoneworld.bungeespeak.util.Replacer;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

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
		BungeeSpeak.getQuery().removeAllEvents();

		if (Configuration.TS_ENABLE_SERVER_EVENTS.getBoolean()) {
			BungeeSpeak.getQuery().addEventNotify(JTS3ServerQuery.EVENT_MODE_SERVER, 0);
		}
		if (Configuration.TS_ENABLE_SERVER_MESSAGES.getBoolean()) {
			BungeeSpeak.getQuery().addEventNotify(JTS3ServerQuery.EVENT_MODE_TEXTSERVER, 0);
		}
		if (Configuration.TS_ENABLE_CHANNEL_EVENTS.getBoolean()) {
			BungeeSpeak.getQuery().addEventNotify(JTS3ServerQuery.EVENT_MODE_CHANNEL,
					BungeeSpeak.getQuery().getCurrentQueryClientChannelID());
		}
		if (Configuration.TS_ENABLE_CHANNEL_MESSAGES.getBoolean()) {
			BungeeSpeak.getQuery().addEventNotify(JTS3ServerQuery.EVENT_MODE_TEXTCHANNEL,
					BungeeSpeak.getQuery().getCurrentQueryClientChannelID());
		}
		if (Configuration.TS_ENABLE_PRIVATE_MESSAGES.getBoolean()) {
			BungeeSpeak.getQuery().addEventNotify(JTS3ServerQuery.EVENT_MODE_TEXTPRIVATE, 0);
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
		int clientId = BungeeSpeak.getQuery().getCurrentQueryClientChannelID();
		HashMap<String, String> info = BungeeSpeak.getQuery().getInfo(JTS3ServerQuery.INFOMODE_CHANNELINFO, clientId);

		mcMsg = new Replacer().addSender(sender).addChannel(info).replace(mcMsg);
		broadcastMessage(mcMsg, sender);
	}

	protected void connectChannel(CommandSender sender) {
		int cid = BungeeSpeak.getQuery().getCurrentQueryClientChannelID();
		int clid = BungeeSpeak.getQuery().getCurrentQueryClientID();
		String pw = Configuration.TS_CHANNEL_PASSWORD.getString();
		if (!BungeeSpeak.getQuery().moveClient(clid, cid, pw)) {
			send(sender, Level.WARNING, "&4The channel ID could not be set.");
			send(sender, Level.WARNING, "&4Ensure that the ChannelID is really assigned to a valid channel.");
			send(sender, Level.WARNING, "&4" + BungeeSpeak.getQuery().getLastError());
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
