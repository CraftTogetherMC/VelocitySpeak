package de.redstoneworld.bungeespeak.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.Commands.Properties.SetAllowLinks;
import de.redstoneworld.bungeespeak.Commands.Properties.SetChannel;
import de.redstoneworld.bungeespeak.Commands.Properties.SetChannelListener;
import de.redstoneworld.bungeespeak.Commands.Properties.SetChannelPassword;
import de.redstoneworld.bungeespeak.Commands.Properties.SetChatListenerPriority;
import de.redstoneworld.bungeespeak.Commands.Properties.SetConsoleLog;
import de.redstoneworld.bungeespeak.Commands.Properties.SetConsoleName;
import de.redstoneworld.bungeespeak.Commands.Properties.SetDebug;
import de.redstoneworld.bungeespeak.Commands.Properties.SetDisplayName;
import de.redstoneworld.bungeespeak.Commands.Properties.SetPrivateMessagesListener;
import de.redstoneworld.bungeespeak.Commands.Properties.SetProperty;
import de.redstoneworld.bungeespeak.Commands.Properties.SetServerListener;
import de.redstoneworld.bungeespeak.Commands.Properties.SetTarget;
import de.redstoneworld.bungeespeak.Commands.Properties.SetTextChannelListener;
import de.redstoneworld.bungeespeak.Commands.Properties.SetTextServerListener;
import de.redstoneworld.bungeespeak.Configuration.Configuration;

import net.md_5.bungee.api.CommandSender;

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

	public void execute(CommandSender sender, String[] args) {
		if (!isConnected(sender)) return;

		if (args.length == 1) {
			send(sender, Level.INFO, "&aUsage: &e/tsa set <property> <value>");
			send(sender, Level.INFO, "&aProperties you can set:");
			send(sender, Level.INFO, props);
		} else if (args.length == 2) {
			SetProperty prop = getMatchingProperty(args[1]);
			if (prop == null) {
				send(sender, Level.WARNING, "&4This is not a valid property.");
				send(sender, Level.WARNING, "&aProperties you can set:");
				send(sender, Level.WARNING, props);
			} else {
				send(sender, Level.INFO, "&4You need to add a value to set.");
				send(sender, Level.INFO, "&aPossible values: &6" + prop.getAllowedInput());
				send(sender, Level.INFO, "&aCurrently set to: &6" + String.valueOf(prop.getProperty().get()));
				send(sender, Level.INFO, "&aDescription:");
				send(sender, Level.INFO, "&6" + prop.getDescription());
			}
		} else if (args.length > 2) {
			String arg = combineSplit(2, args, " ");

			SetProperty prop = getMatchingProperty(args[1]);

			if (prop == null) {
				send(sender, Level.INFO, "&4This is not a valid property.");
				send(sender, Level.INFO, "&aProperties you can set:");
				send(sender, Level.INFO, props);
				return;
			}

			if (!prop.execute(sender, arg)) return;
			send(sender, Level.INFO, "&a" + prop.getName() + " was successfully set to " + arg);
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
	public List<String> onTabComplete(CommandSender sender, String[] args) {
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
				return null;
			} else {
				return prop.onTabComplete(sender, args);
			}
		default:
			return null;
		}
	}
}
