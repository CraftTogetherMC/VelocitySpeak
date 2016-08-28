package de.redstoneworld.bungeespeak.Commands;

import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;

import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.Replacer;
import net.md_5.bungee.api.CommandSender;

public class CommandInfo extends BungeeSpeakCommand {

	public CommandInfo() {
		super("info");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!isConnected(sender)) return;

		String header;
		if (Configuration.MAIN_SERVERPORT.getInt() > 0) {
			header = new Replacer()
					.addAddress(Configuration.MAIN_IP.getString() + ":" + Configuration.MAIN_SERVERPORT.getInt())
					.replace(Messages.MC_COMMAND_INFO_HEADER_PORT.get());
		} else {
			header = new Replacer()
					.addAddress(Configuration.MAIN_IP.getString())
					.addId(-Configuration.MAIN_SERVERPORT.getInt())
					.replace(Messages.MC_COMMAND_INFO_HEADER_VIRTUAL.get());
		}
		int count = 0;
		if (Configuration.MC_COMMANDS_CLIENTLIST_FILTER_INFO.getBoolean()) {
			count = BungeeSpeak.getClientList().getFilteredClients().size();
		} else {
			count = BungeeSpeak.getClientList().size();
		}
		String info = new Replacer()
				.addCount(count)
				.replace(Messages.MC_COMMAND_INFO_TEXT.get());
		send(sender, Level.INFO, header);
		send(sender, Level.INFO, info);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
