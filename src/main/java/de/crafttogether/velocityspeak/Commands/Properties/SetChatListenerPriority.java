package de.crafttogether.velocityspeak.Commands.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;

import de.crafttogether.velocityspeak.Listeners.ChatListener;

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
	public boolean execute(CommandSource source, String arg) {
		ChatListener.Priority p;
		try {
			p = ChatListener.Priority.valueOf(arg.toUpperCase());
		} catch (IllegalArgumentException e) {
			send(source, Level.WARNING, "&4Only LOWEST, LOW, NORMAL, HIGH or HIGHEST are accepted.");
			return false;
		}
		VelocitySpeak.getInstance().getChatListener().setPriority(p);
		PROPERTY.set(p.name());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
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
