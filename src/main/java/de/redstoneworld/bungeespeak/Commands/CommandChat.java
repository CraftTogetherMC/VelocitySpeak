package de.redstoneworld.bungeespeak.Commands;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.Replacer;
import de.redstoneworld.bungeespeak.AsyncQueryUtils.QuerySender;
import de.redstoneworld.bungeespeak.util.MessageUtil;


import net.md_5.bungee.api.CommandSender;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public class CommandChat extends BungeeSpeakCommand {

	public CommandChat() {
		super("chat");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!isConnected(sender)) return;

		if (!Configuration.TS_ENABLE_CHANNEL_MESSAGES.getBoolean()) {
			send(sender, Level.WARNING, "&4You need to enable ListenToChannelChat in the config to use this command.");
			return;
		}

		if (args.length < 2) {
			sendTooFewArgumentsMessage(sender, Messages.MC_COMMAND_CHAT_USAGE.get());
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (String s : Arrays.copyOfRange(args, 1, args.length)) {
			sb.append(s);
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);

		String tsMsg = Messages.MC_COMMAND_CHAT_TS.get();
		String mcMsg = Messages.MC_COMMAND_CHAT_MC.get();

		Replacer r = new Replacer().addSender(sender).addMessage(sb.toString());
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, Configuration.TS_ALLOW_LINKS.getBoolean());
		mcMsg = r.replace(mcMsg);

		if (tsMsg == null || tsMsg.isEmpty()) return;
		QuerySender qs = new QuerySender(BungeeSpeak.getQuery().getCurrentQueryClientChannelID(),
				JTS3ServerQuery.TEXTMESSAGE_TARGET_CHANNEL, tsMsg);
		BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), qs);
		broadcastMessage(mcMsg, sender);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
