package de.redstoneworld.bungeespeak.Commands.Properties;

import java.util.List;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;

import net.md_5.bungee.api.CommandSender;

public class SetDisplayName extends SetProperty {

	private static final Configuration PROPERTY = Configuration.TS_NICKNAME;
	private static final String ALLOWED_INPUT = "Any string";
	private static final String DESCRIPTION = "This name will prefix every message in TeamSpeak. "
			+ "It's the nickname of the server query.";

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
		if (arg.contains(" ")) {
			send(sender, Level.WARNING, "&4The display name can't contain any spaces.");
			return false;
		}
		try {
			BungeeSpeak.getQuery().getApi().setNickname(arg);
			PROPERTY.set(arg);
			return true;
		} catch (TS3CommandFailedException ex) {
			send(sender, Level.WARNING, "&4The display name could not be set.");
			send(sender, Level.WARNING, "&4" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage());
			return false;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
