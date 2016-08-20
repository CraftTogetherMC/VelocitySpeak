package de.redstoneworld.bungeespeak.Commands.Properties;

import java.util.List;

import de.redstoneworld.bungeespeak.Configuration.Configuration;

import net.md_5.bungee.api.CommandSender;

public class SetConsoleName extends SetProperty {

	private static final Configuration PROPERTY = Configuration.TS_CONSOLE_NAME;
	private static final String ALLOWED_INPUT = "Any string";
	private static final String DESCRIPTION = "This name will be used if a message is sent by the console.";

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
		PROPERTY.set(arg);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
