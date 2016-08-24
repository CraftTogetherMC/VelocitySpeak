package de.redstoneworld.bungeespeak.Commands.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;

import de.redstoneworld.bungeespeak.Listeners.ChatListener;
import net.md_5.bungee.api.CommandSender;

public class SetChatListenerPriority extends SetProperty {

	private static final Configuration PROPERTY = Configuration.TS_CHAT_LISTENER_PRIORITY;
	private static final String ALLOWED_INPUT = "LOWEST, LOW, NORMAL, HIGH or HIGHEST";
	private static final String DESCRIPTION = "The priority of the chat listener.";

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
	public boolean execute(CommandSender sender, String arg) {
		ChatListener.Priority p;
		try {
			p = ChatListener.Priority.valueOf(arg.toUpperCase());
		} catch (IllegalArgumentException e) {
			send(sender, Level.WARNING, "&4Only LOWEST, LOW, NORMAL, HIGH or HIGHEST are accepted.");
			return false;
		}
		BungeeSpeak.getInstance().getChatListener().setPriority(p);
		PROPERTY.set(p.name());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length != 3) return null;
		List<String> al = new ArrayList<String>();
		for (ChatListener.Priority p : ChatListener.Priority.values()) {
			if (p.name().toLowerCase().startsWith(args[2].toLowerCase())) {
				al.add(p.name());
			}
		}
		return al;
	}
}
