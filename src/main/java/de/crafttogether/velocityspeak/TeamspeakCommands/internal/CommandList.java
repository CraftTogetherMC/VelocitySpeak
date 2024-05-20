package de.crafttogether.velocityspeak.TeamspeakCommands.internal;

import java.util.Collection;

import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.util.Replacer;
import de.crafttogether.velocityspeak.TeamspeakCommands.TeamspeakCommandSender;
import de.crafttogether.velocityspeak.util.MessageUtil;

public class CommandList extends TeamspeakCommand {

	public CommandList() {
		super("list");
	}

	@Override
	public void execute(TeamspeakCommandSender sender, String[] args) {
		StringBuilder online = new StringBuilder();
		Collection<? extends Player> players = VelocitySpeak.getInstance().getProxy().getAllPlayers();

		if (players.size() > 0) {
			for (Player p : players) {
				online.append(p.getUsername());
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
