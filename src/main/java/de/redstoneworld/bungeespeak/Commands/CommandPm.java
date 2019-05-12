package de.redstoneworld.bungeespeak.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.AsyncQueryUtils.QuerySender;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.util.MessageUtil;
import de.redstoneworld.bungeespeak.util.Replacer;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandPm extends BungeeSpeakCommand {

	public CommandPm() {
		super("pm");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!Configuration.TS_ENABLE_PRIVATE_MESSAGES.getBoolean()) {
			send(sender, Level.WARNING, "&4You need to enable ListenToPrivateMessages in the config to use this command.");
			return;
		}

		if (args.length < 3) {
			sendTooFewArgumentsMessage(sender, Messages.MC_COMMAND_PM_USAGE.get());
			return;
		}

		if (!isConnected(sender)) return;

		Client client = getClient(args[1], sender);
		if (client == null) return;

		StringBuilder sb = new StringBuilder();
		for (String s : Arrays.copyOfRange(args, 2, args.length)) {
			sb.append(s);
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);

		String tsMsg = Messages.MC_COMMAND_PM_TS.get();
		String mcMsg = Messages.MC_COMMAND_PM_MC.get();
		String name;
		if (sender instanceof ProxiedPlayer) {
			name = ((ProxiedPlayer) sender).getName();
		} else {
			name = MessageUtil.toMinecraft(Configuration.TS_CONSOLE_NAME.getString(), false, false);
		}

		Replacer r = new Replacer().addSender(sender).addTargetClient(client.getMap()).addMessage(sb.toString());
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, Configuration.TS_ALLOW_LINKS.getBoolean());
		mcMsg = r.replace(mcMsg);

		if (tsMsg == null || tsMsg.isEmpty()) return;
		int i = client.getId();
		QuerySender qs = new QuerySender(i, TextMessageTargetMode.CLIENT, tsMsg);
		BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), qs);
		BungeeSpeak.registerRecipient(name, i);
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
