package de.crafttogether.velocityspeak.Commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.util.Replacer;
import de.crafttogether.velocityspeak.AsyncQueryUtils.QuerySender;
import de.crafttogether.velocityspeak.util.MessageUtil;

public class CommandChat extends BungeeSpeakCommand {

	public CommandChat() {
		super("chat");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		if (!isConnected(source)) return;

		if (!Configuration.TS_ENABLE_CHANNEL_MESSAGES.getBoolean()) {
			send(source, Level.WARNING, "&4You need to enable ListenToChannelChat in the config to use this command.");
			return;
		}

		if (args.length < 2) {
			sendTooFewArgumentsMessage(source, Messages.MC_COMMAND_CHAT_USAGE.get());
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

		Replacer r = new Replacer().addSender(source).addMessage(sb.toString());
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, Configuration.TS_ALLOW_LINKS.getBoolean());
		mcMsg = r.replace(mcMsg);

		if (tsMsg == null || tsMsg.isEmpty()) return;
		QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getChannelId(),
				TextMessageTargetMode.CHANNEL, tsMsg);
		VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
		broadcastMessage(mcMsg, source);
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		return Collections.emptyList();
	}
}
