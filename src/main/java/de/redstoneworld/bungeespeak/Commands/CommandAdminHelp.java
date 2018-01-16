package de.redstoneworld.bungeespeak.Commands;

import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.Replacer;
import net.md_5.bungee.api.CommandSender;

public class CommandAdminHelp extends BungeeSpeakCommand {

	public CommandAdminHelp() {
		super("help", "adminhelp");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		send(sender, Level.INFO, false, Messages.MC_COMMAND_HELP_ADMIN_HEADER.get());
		sendAdminCommandHelp(sender, "ban", Messages.MC_COMMAND_BAN_DESCRIPTION.get());
		sendAdminCommandHelp(sender, "kick", Messages.MC_COMMAND_KICK_DESCRIPTION.get());
		sendAdminCommandHelp(sender, "channelkick", Messages.MC_COMMAND_CHANNEL_KICK_DESCRIPTION.get());
		sendAdminCommandHelp(sender, "set", Messages.MC_COMMAND_SET_DESCRIPTION.get());
		sendAdminCommandHelp(sender, "status", Messages.MC_COMMAND_STATUS_DESCRIPTION.get());
		sendAdminCommandHelp(sender, "reload", Messages.MC_COMMAND_RELOAD_DESCRIPTION.get());
		send(sender, Level.INFO, false, Messages.MC_COMMAND_HELP_ADMIN_FOOTER.get());
	}

	private void sendAdminCommandHelp(CommandSender sender, String command, String description) {
		if (!checkCommandPermission(sender, command)) return;
		String help = Messages.MC_COMMAND_HELP_ADMIN.get();
		help = new Replacer().addCommandDescription("/tsa " + command, description).replace(help);
		send(sender, Level.INFO, false, help);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
