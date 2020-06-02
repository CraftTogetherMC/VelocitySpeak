package de.redstoneworld.bungeespeak.TeamspeakCommands.internal;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.TeamspeakCommands.TeamspeakCommandSender;
import de.redstoneworld.bungeespeak.util.Replacer;

import de.redstoneworld.bungeespeak.util.MessageUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandPm extends TeamspeakCommand {

	public CommandPm() {
		super("pm", "tell");
	}

	@Override
	public void execute(TeamspeakCommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(Messages.TS_COMMAND_TOO_FEW_ARGUMENTS.get());
			sender.sendMessage(Messages.TS_COMMAND_PM_USAGE.get());
			return;
		}
		String mcUser = args[0];

		ProxiedPlayer p = BungeeSpeak.getInstance().getProxy().getPlayer(mcUser);

		if (p == null) {
			String noUser = Messages.TS_COMMAND_PM_NO_PLAYER_BY_THIS_NAME.get();
			noUser = new Replacer().addClient(sender.getClientInfo()).addInput(mcUser).replace(noUser);
			if (!noUser.isEmpty()) sender.sendMessage(noUser);
			return;
		}

		if (args.length > 1) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				sb.append(args[i]);
				sb.append(" ");
			}
			sb.deleteCharAt(sb.length() - 1);

			String m = Messages.TS_EVENT_PRIVATE_MESSAGE.get();
			m = new Replacer().addClient(sender.getClientInfo()).addMessage(sb.toString()).replace(m);
			m = MessageUtil.toMinecraft(m, true, Configuration.TS_ALLOW_LINKS.getBoolean());

			if (!m.isEmpty()) {
				if (!BungeeSpeak.getMuted(p) && p.hasPermission("bungeespeak.messages.pm")) {
					p.sendMessage(m);

					String tsMsg = Messages.TS_COMMAND_PM.get();
					Replacer r = new Replacer().addClient(sender.getClientInfo()).addMessage(sb.toString()).addPlayer(p);
					tsMsg = r.replace(tsMsg);
					if (!tsMsg.isEmpty()) {
						sender.sendMessage(tsMsg);
					}
				} else {
					String userMuted = Messages.TS_COMMAND_PM_RECIPIENT_MUTED.get();
					userMuted = new Replacer().addClient(sender.getClientInfo()).addPlayer(p).replace(userMuted);
					if (!userMuted.isEmpty()) sender.sendMessage(userMuted);
				}
			}
		}

		String convStarted = Messages.TS_COMMAND_PM_CONVERSATION_STARTED.get();
		convStarted = new Replacer().addClient(sender.getClientInfo()).addPlayer(p).replace(convStarted);
		if (!convStarted.isEmpty()) sender.sendMessage(convStarted);

		BungeeSpeak.registerRecipient(p.getName(), sender.getClientID());
	}
}
