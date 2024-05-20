package de.crafttogether.velocityspeak.Commands;

import java.util.*;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.AsyncQueryUtils.QuerySender;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.util.MessageUtil;
import de.crafttogether.velocityspeak.util.Replacer;
import net.kyori.adventure.text.Component;

public class CommandPm extends BungeeSpeakCommand {

	public CommandPm() {
		super("pm");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		if (!Configuration.TS_ENABLE_PRIVATE_MESSAGES.getBoolean()) {
			send(source, Level.WARNING, "&4You need to enable ListenToPrivateMessages in the config to use this command.");
			return;
		}

		if (args.length < 3) {
			sendTooFewArgumentsMessage(source, Messages.MC_COMMAND_PM_USAGE.get());
			return;
		}

		if (!isConnected(source)) return;

		Client client = getClient(args[1], source);
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
		if (source instanceof Player) {
			name = ((Player) source).getUsername();
		} else {
			name = MessageUtil.toMinecraft(Configuration.TS_CONSOLE_NAME.getString(), false, false);
		}

		Replacer r = new Replacer().addSender(source).addTargetClient(client.getMap()).addMessage(sb.toString());
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, Configuration.TS_ALLOW_LINKS.getBoolean());
		mcMsg = r.replace(mcMsg);

		if (tsMsg == null || tsMsg.isEmpty()) return;
		int i = client.getId();
		QuerySender qs = new QuerySender(i, TextMessageTargetMode.CLIENT, tsMsg);
		VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
		VelocitySpeak.registerRecipient(name, i);
		if (mcMsg == null || mcMsg.isEmpty()) return;
		if (source instanceof Player) {
			source.sendMessage(Component.text(MessageUtil.toMinecraft(mcMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean())));
		} else {
			VelocitySpeak.log().info(MessageUtil.toMinecraft(mcMsg, false, Configuration.TS_ALLOW_LINKS.getBoolean()));
		}
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
