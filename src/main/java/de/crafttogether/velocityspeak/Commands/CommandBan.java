package de.crafttogether.velocityspeak.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.util.MessageUtil;
import de.crafttogether.velocityspeak.util.Replacer;
import de.crafttogether.velocityspeak.AsyncQueryUtils.QueryBan;

public class CommandBan extends BungeeSpeakCommand {

	public CommandBan() {
		super("ban");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		if (args.length < 2) {
			sendTooFewArgumentsMessage(source, Messages.MC_COMMAND_BAN_USAGE.get());
			return;
		}

		if (!isConnected(source)) return;

		Client client = getClient(args[1], source);
		if (client == null) return;

		String tsMsg = Messages.MC_COMMAND_BAN_TS.get();
		String mcMsg = Messages.MC_COMMAND_BAN_MC.get();
		String msg = Messages.MC_COMMAND_DEFAULT_REASON.get();
		if (args.length > 2) {
			msg = combineSplit(2, args, " ");
		}

		Replacer r = new Replacer().addSender(source).addTargetClient(client.getMap()).addMessage(msg);
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), false, Configuration.TS_ALLOW_LINKS.getBoolean());
		mcMsg = r.replace(mcMsg);

		if (tsMsg == null || tsMsg.isEmpty()) return;
		if (tsMsg.length() > TS_MAXLENGHT) {
			String tooLong = Messages.MC_COMMAND_ERROR_MESSAGE_TOO_LONG.get();
			tooLong = new Replacer().addSender(source).addTargetClient(client.getMap()).replace(tooLong);
			send(source, Level.WARNING, tooLong);
			return;
		}

		Integer i = Integer.valueOf(client.get("clid"));
		QueryBan qb = new QueryBan(i, tsMsg);
		VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qb).schedule();
		broadcastMessage(mcMsg, source);
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		if (args.length != 2) return Collections.emptyList();
		List<String> al = new ArrayList<String>();
		for (Client client : VelocitySpeak.getClientList().getClients().values()) {
			String n = client.getNickname().replaceAll(" ", "");
			if (n.toLowerCase().startsWith(args[1].toLowerCase())) {
				al.add(n);
			}
		}
		return al;
	}
}
