package de.crafttogether.velocityspeak.Commands.Properties;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.util.MessageUtil;
import de.crafttogether.velocityspeak.util.Replacer;
import net.kyori.adventure.text.Component;

public abstract class SetProperty {

	protected void send(CommandSource source, Level level, String msg) {
		String m = msg;
		if (source instanceof Player) {
			m = m.replaceAll("((&|$)([a-fk-orA-FK-OR0-9]))", "\u00A7$3");
			source.sendMessage(Component.text(VelocitySpeak.getFullName() + m));
		} else {
			m = m.replaceAll("((&|$|\u00A7)([a-fk-orA-FK-OR0-9]))", "");
			VelocitySpeak.log().log(level, m);
		}
	}

	protected void reloadListener() {
		VelocitySpeak.getQuery().getApi().unregisterAllEvents();

		if (Configuration.TS_ENABLE_SERVER_EVENTS.getBoolean()) {
			VelocitySpeak.getQuery().getApi().registerEvent(TS3EventType.SERVER);
		}
		if (Configuration.TS_ENABLE_SERVER_MESSAGES.getBoolean()) {
			VelocitySpeak.getQuery().getApi().registerEvent(TS3EventType.TEXT_SERVER);
		}
		if (Configuration.TS_ENABLE_CHANNEL_EVENTS.getBoolean()) {
			VelocitySpeak.getQuery().getApi().registerEvent(TS3EventType.CHANNEL,
					VelocitySpeak.getQueryInfo().getChannelId());
		}
		if (Configuration.TS_ENABLE_CHANNEL_MESSAGES.getBoolean()) {
			VelocitySpeak.getQuery().getApi().registerEvent(TS3EventType.TEXT_CHANNEL,
					VelocitySpeak.getQueryInfo().getChannelId());
		}
		if (Configuration.TS_ENABLE_PRIVATE_MESSAGES.getBoolean()) {
			VelocitySpeak.getQuery().getApi().registerEvent(TS3EventType.TEXT_PRIVATE);
		}
	}

	protected void broadcastMessage(String mcMsg, CommandSource source) {
		if (mcMsg == null || mcMsg.isEmpty()) return;
		for (Player pl : VelocitySpeak.getInstance().getProxy().getAllPlayers()) {
			if (!VelocitySpeak.getMuted(pl)) {
				pl.sendMessage(Component.text(MessageUtil.toMinecraft(mcMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean())));
			}
		}
		if (!(source instanceof Player) || (Configuration.TS_LOGGING.getBoolean())) {
			VelocitySpeak.log().info(MessageUtil.toMinecraft(mcMsg, false, Configuration.TS_ALLOW_LINKS.getBoolean()));
		}
	}

	protected void sendChannelChangeMessage(CommandSource source) {
		String mcMsg = Messages.MC_COMMAND_CHANNEL_CHANGE.get();
		int clientId = VelocitySpeak.getQueryInfo().getChannelId();
		Map<String, String> info = VelocitySpeak.getQuery().getApi().getChannelInfo(clientId).getMap();

		mcMsg = new Replacer().addSender(source).addChannel(info).replace(mcMsg);
		broadcastMessage(mcMsg, source);
	}

	protected void connectChannel(CommandSource source) {
		int cid = VelocitySpeak.getQueryInfo().getChannelId();
		String pw = Configuration.TS_CHANNEL_PASSWORD.getString();
		try {
			VelocitySpeak.getQuery().getApi().moveQuery(cid, pw);
		} catch (TS3CommandFailedException ex) {
			send(source, Level.WARNING, "&4The channel ID could not be set.");
			send(source, Level.WARNING, "&4Ensure that the ChannelID is really assigned to a valid channel.");
			send(source, Level.WARNING, "&4" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage());
			return;
		}
		sendChannelChangeMessage(source);
	}

	public String getName() {
		String property = getProperty().getConfigPath();
		return property.substring(property.lastIndexOf(".") + 1);
	}

	public abstract Configuration getProperty();

	public abstract String getAllowedInput();

	public abstract String getDescription();

	public abstract boolean execute(CommandSource source, String arg);

	public abstract List<String> onTabComplete(CommandSource source, String[] args);
}
