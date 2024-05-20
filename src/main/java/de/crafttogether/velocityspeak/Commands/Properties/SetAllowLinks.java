package de.crafttogether.velocityspeak.Commands.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.Configuration.Configuration;

public class SetAllowLinks extends SetProperty {

	private static final Configuration PROPERTY = Configuration.TS_ALLOW_LINKS;
	private static final String ALLOWED_INPUT = "true or false";
	private static final String DESCRIPTION = "If set to false, any links in messages will be filtered out.";
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
	public boolean execute(CommandSource source, String arg) {
		if (arg.equalsIgnoreCase("true")) {
			PROPERTY.set(true);
			send(source, Level.INFO, "&aLinks in messages are now allowed.");
		} else if (arg.equalsIgnoreCase("false")) {
			PROPERTY.set(false);
			send(source, Level.INFO, "&aLinks in messages are now forbidden.");
		} else {
			send(source, Level.WARNING, "&4Only 'true' or 'false' are accepted.");
			return false;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
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
