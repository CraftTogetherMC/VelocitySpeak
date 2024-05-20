package de.crafttogether.velocityspeak.TeamspeakCommands.internal;

import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.TeamspeakCommands.TeamspeakCommandSender;
import de.crafttogether.velocityspeak.util.Replacer;

import de.crafttogether.velocityspeak.util.MessageUtil;
import net.kyori.adventure.text.Component;

import java.util.Optional;

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

		Player p = null;
		Optional<Player> oP = VelocitySpeak.getInstance().getProxy().getPlayer(mcUser);
		if (oP.isPresent()) p = oP.get();

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
				if (!VelocitySpeak.getMuted(p) && p.hasPermission("bungeespeak.messages.pm")) {
					p.sendMessage(Component.text(m));

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

		VelocitySpeak.registerRecipient(p.getUsername(), sender.getClientID());
	}
}
