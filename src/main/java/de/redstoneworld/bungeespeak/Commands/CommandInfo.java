package de.redstoneworld.bungeespeak.Commands;

import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;

import net.md_5.bungee.api.CommandSender;

public class CommandInfo extends BungeeSpeakCommand {

	public CommandInfo() {
		super("info");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!isConnected(sender)) return;

		if (Configuration.MAIN_SERVERPORT.getInt() > 0) {
			StringBuilder sb = new StringBuilder();

			sb.append("&aTeamspeak IP: &e").append(Configuration.MAIN_IP.getString());
			sb.append(":").append(Configuration.MAIN_SERVERPORT.getInt());

			send(sender, Level.INFO, sb.toString());
		} else {
			int port = -Configuration.MAIN_SERVERPORT.getInt();
			StringBuilder sb = new StringBuilder();

			sb.append("&aTeamspeak IP: &e").append(Configuration.MAIN_IP.getString());
			sb.append(", Virtual Server ID: ").append(String.valueOf(port));

			send(sender, Level.INFO, sb.toString());
		}
		send(sender, Level.INFO, "&aClients online: &e" + BungeeSpeak.getClientList().size());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
