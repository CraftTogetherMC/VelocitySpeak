package de.crafttogether.velocityspeak.Commands;

import java.util.*;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.AsyncQueryUtils.QueryPoke;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.util.MessageUtil;
import de.crafttogether.velocityspeak.util.Replacer;
import net.kyori.adventure.text.Component;

public class CommandPoke extends BungeeSpeakCommand {

	public CommandPoke() {
		super("poke");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		if (args.length < 3) {
			sendTooFewArgumentsMessage(source, Messages.MC_COMMAND_POKE_USAGE.get());
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

		String tsMsg = Messages.MC_COMMAND_POKE_TS.get();
		String mcMsg = Messages.MC_COMMAND_POKE_MC.get();

		Replacer r = new Replacer().addSender(source).addTargetClient(client.getMap()).addMessage(sb.toString());
		tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, Configuration.TS_ALLOW_LINKS.getBoolean());
		mcMsg = r.replace(mcMsg);

		if (tsMsg == null || tsMsg.isEmpty()) return;
		if (tsMsg.length() > TS_MAXLENGHT) {
			String tooLong = Messages.MC_COMMAND_ERROR_MESSAGE_TOO_LONG.get();
			tooLong = new Replacer().addSender(source).addTargetClient(client.getMap()).replace(tooLong);
			send(source, Level.WARNING, tooLong);
			return;
		}

		Integer i = Integer.valueOf(client.get("clid"));
		QueryPoke qp = new QueryPoke(i, tsMsg);
		VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qp).schedule();
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
