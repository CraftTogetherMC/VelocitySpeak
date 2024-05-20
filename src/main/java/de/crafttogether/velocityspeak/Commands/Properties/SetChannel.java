package de.crafttogether.velocityspeak.Commands.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;

public class SetChannel extends SetProperty {

	private static final Configuration PROPERTY = Configuration.TS_CHANNEL_ID;
	private static final String ALLOWED_INPUT = "Channel name or ID";
	private static final String DESCRIPTION = "BungeeSpeak will try to move itself into the channel with the stated ID. "
			+ "Set ChannelPassword &lfirst&r&6!";

	@Override
	public Configuration getProperty() {
		return PROPERTY;
	}

	@Override
	public String getAllowedInput() {
		return ALLOWED_INPUT;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public boolean execute(CommandSource source, String arg) {
		if (!(Configuration.TS_ENABLE_CHANNEL_EVENTS.getBoolean())
				&& !(Configuration.TS_ENABLE_CHANNEL_MESSAGES.getBoolean())) {
			send(source, Level.WARNING, "&4Set " + Configuration.TS_ENABLE_CHANNEL_EVENTS.getConfigPath() + " or "
					+ Configuration.TS_ENABLE_CHANNEL_MESSAGES.getConfigPath() + " to true to use this feature.");
			return false;
		}
		if (arg.contains(" ")) {
			send(source, Level.WARNING, "&4The value must be an Integer greater than 0 or the name of a channel.");
			return false;
		}

		ChannelBase channel;
		try {
			channel = VelocitySpeak.getChannelList().getByPartialName(arg);
		} catch (IllegalArgumentException e) {
			channel = null;
		}

		int cid = -1;
		if (channel == null) {
			cid = getIntFromString(arg);
		} else {
			cid = Integer.valueOf(channel.get("cid"));
		}

		if (cid < 1) {
			send(source, Level.WARNING, "&4The value must be an Integer greater than 0.");
			return false;
		}

		String pw = Configuration.TS_CHANNEL_PASSWORD.getString();

		try {
			VelocitySpeak.getQuery().getApi().moveQuery(cid, pw);
			Configuration.TS_CHANNEL_ID.set(cid);
			Configuration.save();
			reloadListener();
			send(source, Level.INFO, "&aThe channel ID was successfully set to " + arg);
			sendChannelChangeMessage(source);
		} catch (TS3CommandFailedException ex) {
			send(source, Level.WARNING, "&4The channel ID could not be set.");
			send(source, Level.WARNING, "&4Ensure that this ID is really assigned to a channel.");
			send(source, Level.WARNING, "&4" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage());
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		if (args.length != 3) return null;
		List<String> al = new ArrayList<String>();
		for (String s : VelocitySpeak.getChannelList().getChannelNames()) {
			if (s.toLowerCase().replaceAll(" ", "").startsWith(args[2].toLowerCase())) {
				al.add(s.replaceAll(" ", ""));
			}
		}
		return al;
	}

	private int getIntFromString(String s) {
		int ret;
		try {
			ret = Integer.valueOf(s);
			return ret;
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}
}
