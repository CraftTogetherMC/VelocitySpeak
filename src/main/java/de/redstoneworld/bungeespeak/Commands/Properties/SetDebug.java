package de.redstoneworld.bungeespeak.Commands.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.BungeeSpeak;

import net.md_5.bungee.api.CommandSender;

public class SetDebug extends SetProperty {

	private static final Configuration PROPERTY = Configuration.TS_DEBUGGING;
	private static final String ALLOWED_INPUT = "true or false";
	private static final String DESCRIPTION = "True sets the plugin to debug mode.";
	private static final String[] TAB_SUGGESTIONS = {"true", "false"};

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
		if (arg.equalsIgnoreCase("true")) {
			PROPERTY.set(true);
			send(sender, Level.INFO, "&aDebug mode was successfully enabled.");
			BungeeSpeak.getInstance().reload();
		} else if (arg.equalsIgnoreCase("false")) {
			PROPERTY.set(false);
			send(sender, Level.INFO, "&aDebug mode was successfully disabled.");
			BungeeSpeak.getInstance().reload();
		} else {
			send(sender, Level.WARNING, "&4Only 'true' or 'false' are accepted.");
			return false;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length != 3) return null;
		List<String> al = new ArrayList<String>();
		for (String s : TAB_SUGGESTIONS) {
			if (s.startsWith(args[2].toLowerCase())) {
				al.add(s);
			}
		}
		return al;
	}
}
