package de.crafttogether.velocityspeak.Commands.Properties;

import java.util.List;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;

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
	public boolean execute(CommandSource source, String arg) {
		if (arg.contains(" ")) {
			send(source, Level.WARNING, "&4The display name can't contain any spaces.");
			return false;
		}
		try {
			VelocitySpeak.getQuery().getApi().setNickname(arg);
			PROPERTY.set(arg);
			return true;
		} catch (TS3CommandFailedException ex) {
			send(source, Level.WARNING, "&4The display name could not be set.");
			send(source, Level.WARNING, "&4" + ex.getError().getId() + ": " + ex.getError().getMessage() + " - "  + ex.getError().getExtraMessage());
			return false;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		return null;
	}
}
