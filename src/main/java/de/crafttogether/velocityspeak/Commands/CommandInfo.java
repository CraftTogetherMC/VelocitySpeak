package de.crafttogether.velocityspeak.Commands;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;

import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.util.Replacer;

public class CommandInfo extends BungeeSpeakCommand {

	public CommandInfo() {
		super("info");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		if (!isConnected(source)) return;

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
			count = VelocitySpeak.getClientList().getFilteredClients().size();
		} else {
			count = VelocitySpeak.getClientList().size();
		}
		String info = new Replacer()
				.addCount(count)
				.replace(Messages.MC_COMMAND_INFO_TEXT.get());
		send(source, Level.INFO, header);
		send(source, Level.INFO, info);
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		return Collections.emptyList();
	}
}
