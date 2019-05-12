package de.redstoneworld.bungeespeak.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.Replacer;
import de.redstoneworld.bungeespeak.AsyncQueryUtils.QueryPoke;
import de.redstoneworld.bungeespeak.util.MessageUtil;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandPoke extends BungeeSpeakCommand {

	public CommandPoke() {
		super("poke");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length < 3) {
			sendTooFewArgumentsMessage(sender, Messages.MC_COMMAND_POKE_USAGE.get());
			return;
		}

		if (!isConnected(sender)) return;

		Client client = getClient(args[1], sender);

		StringBuilder sb = new StringBuilder();
		for (String s : Arrays.copyOfRange(args, 2, args.length)) {
			sb.append(s);
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);

		String tsMsg = Messages.MC_COMMAND_POKE_TS.get();
		String mcMsg = Messages.MC_COMMAND_POKE_MC.get();

		Replacer r = new Replacer().addSender(sender).addTargetClient(client.getMap()).addMessage(sb.toString());
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, Configuration.TS_ALLOW_LINKS.getBoolean());
		mcMsg = r.replace(mcMsg);

		if (tsMsg == null || tsMsg.isEmpty()) return;
		if (tsMsg.length() > TS_MAXLENGHT) {
			String tooLong = Messages.MC_COMMAND_ERROR_MESSAGE_TOO_LONG.get();
			tooLong = new Replacer().addSender(sender).addTargetClient(client.getMap()).replace(tooLong);
			send(sender, Level.WARNING, tooLong);
			return;
		}

		Integer i = Integer.valueOf(client.get("clid"));
		QueryPoke qp = new QueryPoke(i, tsMsg);
		BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), qp);
		if (mcMsg == null || mcMsg.isEmpty()) return;
		if (sender instanceof ProxiedPlayer) {
			sender.sendMessage(MessageUtil.toMinecraft(mcMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean()));
		} else {
			BungeeSpeak.log().info(MessageUtil.toMinecraft(mcMsg, false, Configuration.TS_ALLOW_LINKS.getBoolean()));
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length != 2) return null;
		List<String> al = new ArrayList<String>();
		for (Client client : BungeeSpeak.getClientList().getClients().values()) {
			String n = client.getNickname().replaceAll(" ", "");
			if (n.toLowerCase().startsWith(args[1].toLowerCase())) {
				al.add(n);
			}
		}
		return al;
	}
}
