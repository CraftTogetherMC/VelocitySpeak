package de.redstoneworld.bungeespeak.Commands;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.MessageUtil;
import de.redstoneworld.bungeespeak.util.Replacer;
import de.redstoneworld.bungeespeak.AsyncQueryUtils.QuerySender;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandReply extends BungeeSpeakCommand {

	public CommandReply() {
		super("reply", "r");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length < 2) {
			sendTooFewArgumentsMessage(sender, Messages.MC_COMMAND_REPLY_USAGE.get());
			return;
		}

		if (!isConnected(sender)) return;

		Integer clid;
		if (sender instanceof ProxiedPlayer) {
			clid = BungeeSpeak.getInstance().getSender(((ProxiedPlayer) sender).getName());
		} else {
			String n = MessageUtil.toMinecraft(Configuration.TS_CONSOLE_NAME.getString(), false, false);
			clid = BungeeSpeak.getInstance().getSender(n);
		}

		if (clid == null || !BungeeSpeak.getClientList().containsID(clid)) {
			String noRecipient = Messages.MC_COMMAND_REPLY_NO_RECIPIENT.get();
			noRecipient = new Replacer().addSender(sender).replace(noRecipient);
			send(sender, Level.WARNING, noRecipient);
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (String s : Arrays.copyOfRange(args, 1, args.length)) {
			sb.append(s);
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);

		String tsMsg = Messages.MC_COMMAND_PM_TS.get();
		String mcMsg = Messages.MC_COMMAND_PM_MC.get();

		Replacer r = new Replacer().addSender(sender).addTargetClient(BungeeSpeak.getClientList().get(clid).getMap());
		r.addMessage(sb.toString());
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, Configuration.TS_ALLOW_LINKS.getBoolean());
		mcMsg = r.replace(mcMsg);

		if (tsMsg == null || tsMsg.isEmpty()) return;
		QuerySender qs = new QuerySender(clid, TextMessageTargetMode.CLIENT, tsMsg);
		BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), qs);
		if (mcMsg == null || mcMsg.isEmpty()) return;
		if (sender instanceof ProxiedPlayer) {
			sender.sendMessage(MessageUtil.toMinecraft(mcMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean()));
		} else {
			BungeeSpeak.log().info(MessageUtil.toMinecraft(mcMsg, false, Configuration.TS_ALLOW_LINKS.getBoolean()));
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
