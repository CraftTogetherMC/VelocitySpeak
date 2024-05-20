package de.crafttogether.velocityspeak.Commands;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;

import de.crafttogether.velocityspeak.util.Replacer;

public class CommandHelp extends BungeeSpeakCommand {

	public CommandHelp() {
		super("help");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		send(source, Level.INFO, false, Messages.MC_COMMAND_HELP_USER_HEADER.get());
		sendUserCommandHelp(source, "list", Messages.MC_COMMAND_LIST_DESCRIPTION.get());
		sendUserCommandHelp(source, "mute", Messages.MC_COMMAND_MUTE_DESCRIPTION.get());
		if (Configuration.TS_ENABLE_SERVER_MESSAGES.getBoolean()) {
			sendUserCommandHelp(source, "broadcast", Messages.MC_COMMAND_BROADCAST_DESCRIPTION.get());
		}
		if (Configuration.TS_ENABLE_CHANNEL_MESSAGES.getBoolean()) {
			sendUserCommandHelp(source, "chat", Messages.MC_COMMAND_CHAT_DESCRIPTION.get());
		}
		if (Configuration.TS_ENABLE_PRIVATE_MESSAGES.getBoolean()) {
			sendUserCommandHelp(source, "pm", Messages.MC_COMMAND_PM_DESCRIPTION.get());
			sendUserCommandHelp(source, "reply", "r(eply)", Messages.MC_COMMAND_REPLY_DESCRIPTION.get());
		}
		sendUserCommandHelp(source, "poke", Messages.MC_COMMAND_POKE_DESCRIPTION.get());
		sendUserCommandHelp(source, "info", Messages.MC_COMMAND_INFO_DESCRIPTION.get());

		if (checkCommandPermission(source, "admin")) {
			String help = Messages.MC_COMMAND_HELP_ADMIN.get();
			help = new Replacer().addCommandDescription(Messages.MC_COMMAND_HELP_ADMIN_COMMAND.get(),
					Messages.MC_COMMAND_HELP_ADMIN_DESCRIPTION.get()).replace(help);
			send(source, Level.INFO, false, help);
		}
		send(source, Level.INFO, false, Messages.MC_COMMAND_HELP_USER_FOOTER.get());
	}

	private void sendUserCommandHelp(CommandSource source, String command, String description) {
		sendUserCommandHelp(source, command, command, description);
	}

	private void sendUserCommandHelp(CommandSource source, String permission, String command, String description) {
		if (!checkCommandPermission(source, permission)) return;
		String help = Messages.MC_COMMAND_HELP_USER.get();
		help = new Replacer().addCommandDescription("/ts " + command, description).replace(help);
		send(source, Level.INFO, false, help);
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		return Collections.emptyList();
	}
}
