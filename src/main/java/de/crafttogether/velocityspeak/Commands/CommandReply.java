package de.crafttogether.velocityspeak.Commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.AsyncQueryUtils.QuerySender;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.util.MessageUtil;
import de.crafttogether.velocityspeak.util.Replacer;
import net.kyori.adventure.text.Component;

public class CommandReply extends BungeeSpeakCommand {

	public CommandReply() {
		super("reply", "r");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		if (args.length < 2) {
			sendTooFewArgumentsMessage(source, Messages.MC_COMMAND_REPLY_USAGE.get());
			return;
		}

		if (!isConnected(source)) return;

		Integer clid;
		if (source instanceof Player) {
			clid = VelocitySpeak.getInstance().getSender(((Player) source).getUsername());
		} else {
			String n = MessageUtil.toMinecraft(Configuration.TS_CONSOLE_NAME.getString(), false, false);
			clid = VelocitySpeak.getInstance().getSender(n);
		}

		if (clid == null || !VelocitySpeak.getClientList().containsID(clid)) {
			String noRecipient = Messages.MC_COMMAND_REPLY_NO_RECIPIENT.get();
			noRecipient = new Replacer().addSender(source).replace(noRecipient);
			send(source, Level.WARNING, noRecipient);
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

		Replacer r = new Replacer().addSender(source).addTargetClient(VelocitySpeak.getClientList().get(clid).getMap());
		r.addMessage(sb.toString());
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, Configuration.TS_ALLOW_LINKS.getBoolean());
		mcMsg = r.replace(mcMsg);

		if (tsMsg == null || tsMsg.isEmpty()) return;
		QuerySender qs = new QuerySender(clid, TextMessageTargetMode.CLIENT, tsMsg);
		VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
		if (mcMsg == null || mcMsg.isEmpty()) return;
		if (source instanceof Player) {
			source.sendMessage(Component.text(MessageUtil.toMinecraft(mcMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean())));
		} else {
			VelocitySpeak.log().info(MessageUtil.toMinecraft(mcMsg, false, Configuration.TS_ALLOW_LINKS.getBoolean()));
		}
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		return Collections.emptyList();
	}
}
