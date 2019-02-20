package de.redstoneworld.bungeespeak;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.List;

import de.redstoneworld.bungeespeak.Commands.BungeeSpeakCommand;
import de.redstoneworld.bungeespeak.Commands.CommandAdminHelp;
import de.redstoneworld.bungeespeak.Commands.CommandBan;
import de.redstoneworld.bungeespeak.Commands.CommandChannelKick;
import de.redstoneworld.bungeespeak.Commands.CommandKick;
import de.redstoneworld.bungeespeak.Commands.CommandReload;
import de.redstoneworld.bungeespeak.Commands.CommandSet;
import de.redstoneworld.bungeespeak.Commands.CommandStatus;

import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.TeamspeakCommands.TeamspeakCommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class TsaCommandExecutor extends Command implements TabExecutor {

	private List<BungeeSpeakCommand> adminCommands;

	public TsaCommandExecutor() {
		super("bungeespeakadmin", null, "tsa");
		adminCommands = new ArrayList<BungeeSpeakCommand>();
		adminCommands.add(new CommandAdminHelp());
		adminCommands.add(new CommandBan());
		adminCommands.add(new CommandChannelKick());
		adminCommands.add(new CommandKick());
		adminCommands.add(new CommandReload());
		adminCommands.add(new CommandSet());
		adminCommands.add(new CommandStatus());
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!onTeamspeakAdminCommand(sender, args)) {
			send(sender, Level.WARNING, Messages.MC_COMMAND_USAGE_TSA.get());
		}
	}
	
	public void send(CommandSender sender, Level level, String msg) {
		send(sender, level, true, msg);
	}
	
	public void send(CommandSender sender, Level level, boolean prefix, String msg) {
		if (msg.isEmpty()) {
			return;
		}
		if (sender instanceof ProxiedPlayer || sender instanceof TeamspeakCommandSender) {
			sender.sendMessage((prefix ? BungeeSpeak.getFullName() : "") + ChatColor.translateAlternateColorCodes('&', msg));
		} else {
			BungeeSpeak.getInstance().getLogger().log(level, ChatColor.translateAlternateColorCodes('&', msg));
		}
	}

	public Boolean checkPermissions(CommandSender sender, String perm) {
		return sender.hasPermission("bungeespeak.commands." + perm);
	}

	public boolean onTeamspeakAdminCommand(CommandSender sender, String[] args) {

		String s = "adminhelp";
		if (args.length > 0) {
			s = args[0];
		}

		for (BungeeSpeakCommand bsc : adminCommands) {
			for (String name : bsc.getNames()) {
				if (name.equalsIgnoreCase(s)) {
					if (!checkPermissions(sender, bsc.getName())) return false;
					bsc.execute(sender, args);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

		switch (args.length) {
			case 0:
				return null;
			case 1:
				List<String> al = new ArrayList<String>();
				for (BungeeSpeakCommand ac : adminCommands) {
					if (ac.getName().startsWith(args[0].toLowerCase())) {
						if (checkPermissions(sender, ac.getName())) al.add(ac.getName());
					}
				}
				return al;
			default:
				for (BungeeSpeakCommand bsc : adminCommands) {
					for (String name : bsc.getNames()) {
						if (name.equalsIgnoreCase(args[0])) {
							if (!checkPermissions(sender, bsc.getName())) return null;
							return bsc.onTabComplete(sender, args);
						}
					}
				}
				return null;
		}
	}
}
