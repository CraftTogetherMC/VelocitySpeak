package de.crafttogether.velocityspeak.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.Commands.Properties.SetAllowLinks;
import de.crafttogether.velocityspeak.Commands.Properties.SetChannel;
import de.crafttogether.velocityspeak.Commands.Properties.SetChannelListener;
import de.crafttogether.velocityspeak.Commands.Properties.SetChannelPassword;
import de.crafttogether.velocityspeak.Commands.Properties.SetChatListenerPriority;
import de.crafttogether.velocityspeak.Commands.Properties.SetConsoleLog;
import de.crafttogether.velocityspeak.Commands.Properties.SetConsoleName;
import de.crafttogether.velocityspeak.Commands.Properties.SetDebug;
import de.crafttogether.velocityspeak.Commands.Properties.SetDisplayName;
import de.crafttogether.velocityspeak.Commands.Properties.SetPrivateMessagesListener;
import de.crafttogether.velocityspeak.Commands.Properties.SetProperty;
import de.crafttogether.velocityspeak.Commands.Properties.SetServerListener;
import de.crafttogether.velocityspeak.Commands.Properties.SetTarget;
import de.crafttogether.velocityspeak.Commands.Properties.SetTextChannelListener;
import de.crafttogether.velocityspeak.Commands.Properties.SetTextServerListener;
import de.crafttogether.velocityspeak.Configuration.Configuration;

public class CommandSet extends BungeeSpeakCommand {

	private static final SetProperty[] PROPERTIES = {new SetDisplayName(), new SetConsoleName(), new SetChannel(),
			new SetChannelPassword(), new SetServerListener(), new SetTextServerListener(), new SetChannelListener(),
			new SetTextChannelListener(), new SetPrivateMessagesListener(), new SetAllowLinks(), new SetTarget(),
			new SetConsoleLog(), new SetChatListenerPriority(), new SetDebug()};

	private String props;

	public CommandSet() {
		super("set");
		StringBuilder sb = new StringBuilder();
		for (SetProperty prop : PROPERTIES) {
			if (sb.length() > 0) sb.append("&a, ");
			sb.append("&6");
			sb.append(prop.getName());
		}
		props = sb.toString();
	}

	public void execute(CommandSource source, String[] args) {
		if (!isConnected(source)) return;

		if (args.length == 1) {
			send(source, Level.INFO, "&aUsage: &e/tsa set <property> <value>");
			send(source, Level.INFO, "&aProperties you can set:");
			send(source, Level.INFO, props);
		} else if (args.length == 2) {
			SetProperty prop = getMatchingProperty(args[1]);
			if (prop == null) {
				send(source, Level.WARNING, "&4This is not a valid property.");
				send(source, Level.WARNING, "&aProperties you can set:");
				send(source, Level.WARNING, props);
			} else {
				send(source, Level.INFO, "&4You need to add a value to set.");
				send(source, Level.INFO, "&aPossible values: &6" + prop.getAllowedInput());
				send(source, Level.INFO, "&aCurrently set to: &6" + String.valueOf(prop.getProperty().get()));
				send(source, Level.INFO, "&aDescription:");
				send(source, Level.INFO, "&6" + prop.getDescription());
			}
		} else if (args.length > 2) {
			String arg = combineSplit(2, args, " ");

			SetProperty prop = getMatchingProperty(args[1]);

			if (prop == null) {
				send(source, Level.INFO, "&4This is not a valid property.");
				send(source, Level.INFO, "&aProperties you can set:");
				send(source, Level.INFO, props);
				return;
			}

			if (!prop.execute(source, arg)) return;
			send(source, Level.INFO, "&a" + prop.getName() + " was successfully set to " + arg);
			Configuration.save();
		}
	}

	private SetProperty getMatchingProperty(String name) {

		for (SetProperty property : PROPERTIES) {
			if (property.getName().equalsIgnoreCase(name)) {
				return property;
			}
		}
		return null;
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		switch (args.length) {
		case 2:
			String s = args[1].toLowerCase();
			List<String> al = new ArrayList<String>();
			for (SetProperty prop : PROPERTIES) {
				if (prop.getName().toLowerCase().startsWith(s)) {
					al.add(prop.getName());
				}
			}
			return al;
		case 3:
			SetProperty prop = getMatchingProperty(args[1]);

			if (prop == null) {
				return Collections.emptyList();
			} else {
				return prop.onTabComplete(source, args);
			}
		default:
			return Collections.emptyList();
		}
	}
}
