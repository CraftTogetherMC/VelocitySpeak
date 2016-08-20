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

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TsaCommandExecutor extends Command {

	private List<BungeeSpeakCommand> userCommands;
	private List<BungeeSpeakCommand> adminCommands;

	public TsaCommandExecutor() {
		super("tsa");
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
			sender.sendMessage(TextComponent.fromLegacyText("Type /" + getName() + " for help."));
		}
	}

	public void send(CommandSender sender, Level level, String msg) {
		String m = msg;
		if (sender instanceof ProxiedPlayer) {
			m = m.replaceAll("&", "\u00A7").replaceAll("$", "\u00A7");
			sender.sendMessage(BungeeSpeak.getFullName() + m);
		} else {
			m = m.replaceAll("&[a-fA-F0-9]", "").replaceAll("$[a-fA-F0-9]", "");
			BungeeSpeak.log().log(level, m);
		}
	}

	public Boolean checkPermissions(CommandSender sender, String perm) {
		return sender.hasPermission("bukkitspeak.commands." + perm);
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

	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

		switch (args.length) {
			case 0:
				return null;
			case 1:
				if (cmd.getName().equals("ts")) {
					List<String> al = new ArrayList<String>();
					for (BungeeSpeakCommand uc : userCommands) {
						if (uc.getName().startsWith(args[0].toLowerCase())) {
							if (checkPermissions(sender, uc.getName())) al.add(uc.getName());
						}
					}
					return al;
				} else if (cmd.getName().equals("tsa")) {
					List<String> al = new ArrayList<String>();
					for (BungeeSpeakCommand ac : adminCommands) {
						if (ac.getName().startsWith(args[0].toLowerCase())) {
							if (checkPermissions(sender, ac.getName())) al.add(ac.getName());
						}
					}
					return al;
				} else {
					return null;
				}
			default:
				if (cmd.getName().equals("ts")) {
					for (BungeeSpeakCommand bsc : userCommands) {
						for (String name : bsc.getNames()) {
							if (name.equalsIgnoreCase(args[0])) {
								if (!checkPermissions(sender, bsc.getName())) return null;
								return bsc.onTabComplete(sender, args);
							}
						}
					}
					return null;
				} else if (cmd.getName().equals("tsa")) {
					for (BungeeSpeakCommand bsc : adminCommands) {
						for (String name : bsc.getNames()) {
							if (name.equalsIgnoreCase(args[0])) {
								if (!checkPermissions(sender, bsc.getName())) return null;
								return bsc.onTabComplete(sender, args);
							}
						}
					}
					return null;
				} else {
					return null;
				}
		}
	}
}
