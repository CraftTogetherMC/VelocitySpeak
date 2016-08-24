package de.redstoneworld.bungeespeak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.List;

import de.redstoneworld.bungeespeak.Commands.BungeeSpeakCommand;
import de.redstoneworld.bungeespeak.Commands.CommandBroadcast;
import de.redstoneworld.bungeespeak.Commands.CommandChat;
import de.redstoneworld.bungeespeak.Commands.CommandHelp;
import de.redstoneworld.bungeespeak.Commands.CommandInfo;
import de.redstoneworld.bungeespeak.Commands.CommandList;
import de.redstoneworld.bungeespeak.Commands.CommandMute;
import de.redstoneworld.bungeespeak.Commands.CommandPm;
import de.redstoneworld.bungeespeak.Commands.CommandPoke;
import de.redstoneworld.bungeespeak.Commands.CommandReply;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class TsCommandExecutor extends Command implements TabExecutor {

	private List<BungeeSpeakCommand> userCommands;

	public TsCommandExecutor() {
		super("ts");
		userCommands = new ArrayList<BungeeSpeakCommand>();
		userCommands.add(new CommandHelp());
		userCommands.add(new CommandInfo());
		userCommands.add(new CommandList());
		userCommands.add(new CommandMute());
		userCommands.add(new CommandBroadcast());
		userCommands.add(new CommandChat());
		userCommands.add(new CommandPm());
		userCommands.add(new CommandPoke());
		userCommands.add(new CommandReply());
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		boolean success;
		if (args.length >= 1 && args[0].equals("admin")) {
			success = BungeeSpeak.getInstance().getTeamspeakAdminCommand().onTeamspeakAdminCommand(sender, Arrays.copyOfRange(args, 1, args.length));
		} else {
			success = onTeamspeakCommand(sender, args);
		}
		if (!success) {
			sender.sendMessage(TextComponent.fromLegacyText("Type /" + getName() +" for help."));
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
		return sender.hasPermission("bungeespeak.commands." + perm);
	}

	public boolean onTeamspeakCommand(CommandSender sender, String[] args) {

		String s = "help";
		if (args.length > 0) {
			s = args[0];
		}

		for (BungeeSpeakCommand bsc : userCommands) {
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
				for (BungeeSpeakCommand uc : userCommands) {
					if (uc.getName().startsWith(args[0].toLowerCase())) {
						if (checkPermissions(sender, uc.getName())) al.add(uc.getName());
					}
				}
				return al;
			default:
				for (BungeeSpeakCommand bsc : userCommands) {
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
