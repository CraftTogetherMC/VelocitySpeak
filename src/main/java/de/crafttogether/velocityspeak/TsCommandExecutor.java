package de.crafttogether.velocityspeak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.List;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.TeamspeakCommands.TeamspeakCommandSender;
import de.crafttogether.velocityspeak.Commands.BungeeSpeakCommand;
import de.crafttogether.velocityspeak.Commands.CommandBroadcast;
import de.crafttogether.velocityspeak.Commands.CommandChat;
import de.crafttogether.velocityspeak.Commands.CommandHelp;
import de.crafttogether.velocityspeak.Commands.CommandInfo;
import de.crafttogether.velocityspeak.Commands.CommandList;
import de.crafttogether.velocityspeak.Commands.CommandMute;
import de.crafttogether.velocityspeak.Commands.CommandPm;
import de.crafttogether.velocityspeak.Commands.CommandPoke;
import de.crafttogether.velocityspeak.Commands.CommandReply;
import de.crafttogether.velocityspeak.util.ChatColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TsCommandExecutor implements SimpleCommand {

	private List<BungeeSpeakCommand> userCommands;

	public TsCommandExecutor() {
		//super("bungeespeak", null, "ts");
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
	public void execute(Invocation invocation) {
		boolean success;
		if (invocation.arguments().length >= 1 && invocation.arguments()[0].equals("admin")) {
			success = VelocitySpeak.getInstance().getTeamspeakAdminCommand().onTeamspeakAdminCommand(invocation.source(), Arrays.copyOfRange(invocation.arguments(), 1, invocation.arguments().length));
		} else {
			success = onTeamspeakCommand(invocation.source(), invocation.arguments());
		}
		if (!success) {
			send(invocation.source(), Level.WARNING, Messages.MC_COMMAND_USAGE_TS.get());
		}
	}

	public void send(CommandSource source, Level level, String msg) {
		send(source, level, true, msg);
	}

	public void send(CommandSource source, Level level, boolean prefix, String msg) {
		if (msg.isEmpty()) {
			return;
		}
		if (source instanceof Player || source instanceof TeamspeakCommandSender) {
			source.sendMessage(Component.text((prefix ? VelocitySpeak.getFullName() : "") + ChatColor.translateAlternateColorCodes('&', msg)));
		} else {
			VelocitySpeak.getInstance().getLogger().log(level, ChatColor.translateAlternateColorCodes('&', msg));
		}
	}

	public Boolean checkPermissions(CommandSource source, String perm) {
		return source.hasPermission("bungeespeak.commands." + perm);
	}

	public boolean onTeamspeakCommand(CommandSource source, String[] args) {

		String s = "help";
		if (args.length > 0) {
			s = args[0];
		}

		for (BungeeSpeakCommand bsc : userCommands) {
			for (String name : bsc.getNames()) {
				if (name.equalsIgnoreCase(s)) {
					if (!checkPermissions(source, bsc.getName())) return false;
					bsc.execute(source, args);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {

		switch (invocation.arguments().length) {
			case 0:
				return CompletableFuture.completedFuture(Collections.emptyList());
			case 1:
				List<String> al = new ArrayList<String>();
				for (BungeeSpeakCommand uc : userCommands) {
					if (uc.getName().startsWith(invocation.arguments()[0].toLowerCase())) {
						if (checkPermissions(invocation.source(), uc.getName())) al.add(uc.getName());
					}
				}
				return CompletableFuture.completedFuture(al);
			default:
				for (BungeeSpeakCommand bsc : userCommands) {
					for (String name : bsc.getNames()) {
						if (name.equalsIgnoreCase(invocation.arguments()[0])) {
							if (!checkPermissions(invocation.source(), bsc.getName())) return CompletableFuture.completedFuture(Collections.emptyList());
							return CompletableFuture.completedFuture(bsc.onTabComplete(invocation.source(), invocation.arguments()));
						}
					}
				}
				return CompletableFuture.completedFuture(Collections.emptyList());
		}
	}
}
