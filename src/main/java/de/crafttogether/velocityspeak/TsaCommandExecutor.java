package de.crafttogether.velocityspeak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.List;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.Commands.CommandAdminHelp;
import de.crafttogether.velocityspeak.Commands.CommandBan;
import de.crafttogether.velocityspeak.Commands.CommandKick;
import de.crafttogether.velocityspeak.Commands.CommandSet;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.TeamspeakCommands.TeamspeakCommandSender;
import de.crafttogether.velocityspeak.Commands.BungeeSpeakCommand;
import de.crafttogether.velocityspeak.Commands.CommandChannelKick;
import de.crafttogether.velocityspeak.Commands.CommandReload;
import de.crafttogether.velocityspeak.Commands.CommandStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TsaCommandExecutor implements SimpleCommand {

	private List<BungeeSpeakCommand> adminCommands;

	public TsaCommandExecutor() {
		//super("bungeespeakadmin", null, "tsa");
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
	public void execute(Invocation invocation) {
		if (!onTeamspeakAdminCommand(invocation.source(), invocation.arguments())) {
			send(invocation.source(), Level.WARNING, Messages.MC_COMMAND_USAGE_TSA.get());
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
			source.sendMessage(Component.text(prefix ? VelocitySpeak.getFullName() : "")
					.append(LegacyComponentSerializer.legacyAmpersand().deserialize("msg")));
		} else {
			VelocitySpeak.getInstance().getLogger().log(level, msg); // TODO: Strip colorcodes
		}
	}

	public Boolean checkPermissions(CommandSource source, String perm) {
		return source.hasPermission("bungeespeak.commands." + perm);
	}

	public boolean onTeamspeakAdminCommand(CommandSource source, String[] args) {

		String s = "adminhelp";
		if (args.length > 0) {
			s = args[0];
		}

		for (BungeeSpeakCommand bsc : adminCommands) {
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
				for (BungeeSpeakCommand ac : adminCommands) {
					if (ac.getName().startsWith(invocation.arguments()[0].toLowerCase())) {
						if (checkPermissions(invocation.source(), ac.getName())) al.add(ac.getName());
					}
				}
				return CompletableFuture.completedFuture(al);
			default:
				for (BungeeSpeakCommand bsc : adminCommands) {
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
