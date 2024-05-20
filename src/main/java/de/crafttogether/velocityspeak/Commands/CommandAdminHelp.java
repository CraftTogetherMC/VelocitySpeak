package de.crafttogether.velocityspeak.Commands;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.util.Replacer;

public class CommandAdminHelp extends BungeeSpeakCommand {

	public CommandAdminHelp() {
		super("help", "adminhelp");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		send(source, Level.INFO, false, Messages.MC_COMMAND_HELP_ADMIN_HEADER.get());
		sendAdminCommandHelp(source, "ban", Messages.MC_COMMAND_BAN_DESCRIPTION.get());
		sendAdminCommandHelp(source, "kick", Messages.MC_COMMAND_KICK_DESCRIPTION.get());
		sendAdminCommandHelp(source, "channelkick", Messages.MC_COMMAND_CHANNEL_KICK_DESCRIPTION.get());
		sendAdminCommandHelp(source, "set", Messages.MC_COMMAND_SET_DESCRIPTION.get());
		sendAdminCommandHelp(source, "status", Messages.MC_COMMAND_STATUS_DESCRIPTION.get());
		sendAdminCommandHelp(source, "reload", Messages.MC_COMMAND_RELOAD_DESCRIPTION.get());
		send(source, Level.INFO, false, Messages.MC_COMMAND_HELP_ADMIN_FOOTER.get());
	}

	private void sendAdminCommandHelp(CommandSource source, String command, String description) {
		if (!checkCommandPermission(source, command)) return;
		String help = Messages.MC_COMMAND_HELP_ADMIN.get();
		help = new Replacer().addCommandDescription("/tsa " + command, description).replace(help);
		send(source, Level.INFO, false, help);
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		return Collections.emptyList();
	}
}
