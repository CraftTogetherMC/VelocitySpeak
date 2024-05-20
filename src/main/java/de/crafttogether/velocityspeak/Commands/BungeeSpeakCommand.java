package de.crafttogether.velocityspeak.Commands;

import java.util.List;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.TeamspeakCommands.TeamspeakCommandSender;
import de.crafttogether.velocityspeak.util.MessageUtil;
import de.crafttogether.velocityspeak.util.Replacer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

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

	protected void send(CommandSource source, Level level, String msg) {
		send(source, level, true, msg);
	}

	protected void send(CommandSource source, Level level, boolean prefix, String msg) {
		if (msg.isEmpty()) {
			return;
		}

		if (source instanceof Player || source instanceof TeamspeakCommandSender) {
			source.sendMessage(Component.text(prefix ? VelocitySpeak.getFullName() : "")
					.append(LegacyComponentSerializer.legacyAmpersand().deserialize("msg")));
		} else {
			VelocitySpeak.getInstance().getLogger().log(level, msg); // TODO: Strip colorcodes
		}
	}

	protected void broadcastMessage(String mcMsg, CommandSource source) {
		if (mcMsg == null || mcMsg.isEmpty()) return;
		for (Player pl : VelocitySpeak.getInstance().getProxy().getAllPlayers()) {
			if (!VelocitySpeak.getMuted(pl)) {
				pl.sendMessage(Component.text(
						MessageUtil.toMinecraft(mcMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean())));
			}
		}
		if (!(source instanceof Player) || (Configuration.TS_LOGGING.getBoolean())) {
			VelocitySpeak.log().info(MessageUtil.toMinecraft(mcMsg, false, Configuration.TS_ALLOW_LINKS.getBoolean()));
		}
	}

	protected void sendTooFewArgumentsMessage(CommandSource source, String usage) {
		Replacer r = new Replacer().addSender(source);
		String tooFewArgsMessage = r.replace(Messages.MC_COMMAND_ERROR_MESSAGE_TOO_FEW_ARGS.get());
		r.addCommandUsage(usage);
		String usageMessage = r.replace(Messages.MC_COMMAND_ERROR_MESSAGE_USAGE.get());

		send(source, Level.WARNING, tooFewArgsMessage);
		send(source, Level.WARNING, usageMessage);
	}

	protected boolean checkCommandPermission(CommandSource source, String perm) {
		return source.hasPermission("bungeespeak.commands." + perm);
	}

	protected boolean isConnected(CommandSource source) {
		if (!VelocitySpeak.getQuery().isConnected()) {
			String mcMsg = Messages.MC_COMMAND_ERROR_DISCONNECTED.get();
			mcMsg = new Replacer().addSender(source).replace(mcMsg);
			send(source, Level.WARNING, mcMsg);
			return false;
		}
		return true;
	}

	protected Client getClient(String name, CommandSource source) {
		Client client;
		try {
			client = VelocitySpeak.getClientList().getByPartialName(name);
			if (client == null) {
				String noPlayer = Messages.MC_COMMAND_ERROR_NO_PLAYER_FOUND.get();
				noPlayer = new Replacer().addInput(name).replace(noPlayer);
				send(source, Level.WARNING, noPlayer);
				return null;
			}
			return client;
		} catch (IllegalArgumentException e) {
			String multiplePlayers = Messages.MC_COMMAND_ERROR_MULTIPLE_PLAYERS_FOUND.get();
			multiplePlayers = new Replacer().addInput(name).replace(multiplePlayers);
			send(source, Level.WARNING, multiplePlayers);
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

	public abstract void execute(CommandSource source, String[] args);

	public abstract List<String> onTabComplete(CommandSource source, String[] args);
}
