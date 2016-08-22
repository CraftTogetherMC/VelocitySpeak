package de.redstoneworld.bungeespeak.Commands;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.Replacer;
import de.redstoneworld.bungeespeak.util.MessageUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class BungeeSpeakCommand {

	protected static final int TS_MAXLENGHT = 100;
	private final String[] names;

	protected BungeeSpeakCommand(String firstName, String... otherNames) {
		if (firstName == null || firstName.isEmpty()) {
			throw new IllegalArgumentException("A Command did not have a name specified.");
		}

		if (otherNames == null) {
			names = new String[] {firstName};
		} else {
			names = new String[otherNames.length + 1];
			names[0] = firstName;
			for (int i = 0; i < otherNames.length; i++) {
				names[i + 1] = otherNames[i];
			}
		}
	}

	protected void send(CommandSender sender, Level level, String msg) {
		String m = msg;
		if (sender instanceof ProxiedPlayer) {
			m = m.replaceAll("((&|$)([a-fk-orA-FK-OR0-9]))", "\u00A7$3");
			sender.sendMessage(BungeeSpeak.getFullName() + m);
		} else {
			m = (BungeeSpeak.getFullName() + m).replaceAll("((&|$|\u00A7)([a-fk-orA-FK-OR0-9]))", "");
			sender.sendMessage(m);
		}
	}

	protected void broadcastMessage(String mcMsg, CommandSender sender) {
		if (mcMsg == null || mcMsg.isEmpty()) return;
		for (ProxiedPlayer pl : BungeeSpeak.getInstance().getProxy().getPlayers()) {
			if (!BungeeSpeak.getMuted(pl)) {
				pl.sendMessage(MessageUtil.toMinecraft(mcMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean()));
			}
		}
		if (!(sender instanceof ProxiedPlayer) || (Configuration.TS_LOGGING.getBoolean())) {
			BungeeSpeak.log().info(MessageUtil.toMinecraft(mcMsg, false, Configuration.TS_ALLOW_LINKS.getBoolean()));
		}
	}

	protected void sendTooFewArgumentsMessage(CommandSender sender, String usage) {
		Replacer r = new Replacer().addSender(sender);
		String tooFewArgsMessage = r.replace(Messages.MC_COMMAND_ERROR_MESSAGE_TOO_FEW_ARGS.get());
		r.addCommandUsage(usage);
		String usageMessage = r.replace(Messages.MC_COMMAND_ERROR_MESSAGE_USAGE.get());

		send(sender, Level.WARNING, tooFewArgsMessage);
		send(sender, Level.WARNING, usageMessage);
	}

	protected boolean checkCommandPermission(CommandSender sender, String perm) {
		return sender.hasPermission("bungeespeak.commands." + perm);
	}

	protected boolean isConnected(CommandSender sender) {
		if (!BungeeSpeak.getQuery().isConnected()) {
			String mcMsg = Messages.MC_COMMAND_ERROR_DISCONNECTED.get();
			mcMsg = new Replacer().addSender(sender).replace(mcMsg);
			send(sender, Level.WARNING, mcMsg);
			return false;
		}
		return true;
	}

	protected HashMap<String, String> getClient(String name, CommandSender sender) {
		HashMap<String, String> client;
		try {
			client = BungeeSpeak.getClientList().getByPartialName(name);
			if (client == null) {
				String noPlayer = Messages.MC_COMMAND_ERROR_NO_PLAYER_FOUND.get();
				noPlayer = new Replacer().addInput(name).replace(noPlayer);
				send(sender, Level.WARNING, noPlayer);
				return null;
			}
			return client;
		} catch (IllegalArgumentException e) {
			String multiplePlayers = Messages.MC_COMMAND_ERROR_MULTIPLE_PLAYERS_FOUND.get();
			multiplePlayers = new Replacer().addInput(name).replace(multiplePlayers);
			send(sender, Level.WARNING, multiplePlayers);
			return null;
		}
	}

	protected String combineSplit(int startIndex, String[] string, String seperator) {
		StringBuilder builder = new StringBuilder();

		for (int i = startIndex; i < string.length; i++) {
			builder.append(string[i]);
			builder.append(seperator);
		}

		builder.deleteCharAt(builder.length() - seperator.length());
		return builder.toString();
	}

	public final String getName() {
		return names[0];
	}

	public final String[] getNames() {
		return names;
	}

	public abstract void execute(CommandSender sender, String[] args);

	public abstract List<String> onTabComplete(CommandSender sender, String[] args);
}
