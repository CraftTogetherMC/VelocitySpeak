package de.redstoneworld.bungeespeak.TeamspeakCommands.internal;

import java.util.Collection;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.Replacer;
import de.redstoneworld.bungeespeak.TeamspeakCommands.TeamspeakCommandSender;
import de.redstoneworld.bungeespeak.util.MessageUtil;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandList extends TeamspeakCommand {

	public CommandList() {
		super("list");
	}

	@Override
	public void execute(TeamspeakCommandSender sender, String[] args) {
		StringBuilder online = new StringBuilder();
		Collection<? extends ProxiedPlayer> players = BungeeSpeak.getInstance().getProxy().getPlayers();

		if (players.size() > 0) {
			for (ProxiedPlayer p : players) {
				online.append(p.getName());
				online.append(", ");
			}

			online.setLength(online.length() - 2);
		} else {
			online.append(" -");
		}

		String tsMsg = Messages.TS_COMMAND_LIST.get();
		String list = online.toString();

		tsMsg = new Replacer().addSender(sender).addList(list).addCount(players.size()).replace(tsMsg);
		tsMsg = MessageUtil.toTeamspeak(tsMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean());

		if (tsMsg.isEmpty()) return;
		sender.sendMessage(tsMsg);
	}
}
