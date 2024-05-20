package de.crafttogether.velocityspeak.Commands;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.util.Replacer;

public class CommandMute extends BungeeSpeakCommand {

	public CommandMute() {
		super("mute");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		if (source instanceof Player) {
			if (VelocitySpeak.getMuted((Player) source)) {
				VelocitySpeak.setMuted((Player) source, false);

				String mcMsg = Messages.MC_COMMAND_UNMUTE.get();
				mcMsg = new Replacer().addPlayer((Player) source).replace(mcMsg);

				if (mcMsg == null || mcMsg.isEmpty()) return;
				send(source, Level.INFO, mcMsg);
			} else {
				VelocitySpeak.setMuted((Player) source, true);

				String mcMsg = Messages.MC_COMMAND_MUTE.get();
				mcMsg = new Replacer().addPlayer((Player) source).replace(mcMsg);

				if (mcMsg == null || mcMsg.isEmpty()) return;
				send(source, Level.INFO, mcMsg);
			}
		} else {
			send(source, Level.INFO, "Can only mute BungeeSpeak for players!");
		}
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		return Collections.emptyList();
	}
}
