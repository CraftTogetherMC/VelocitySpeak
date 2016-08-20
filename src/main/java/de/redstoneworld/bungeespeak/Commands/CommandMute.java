package de.redstoneworld.bungeespeak.Commands;

import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.Replacer;

import net.md_5.bungee.api.CommandSender;
import org.bukkit.entity.Player;

public class CommandMute extends BungeeSpeakCommand {

	public CommandMute() {
		super("mute");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (BungeeSpeak.getMuted((Player) sender)) {
				BungeeSpeak.setMuted((Player) sender, false);

				String mcMsg = Messages.MC_COMMAND_UNMUTE.get();
				mcMsg = new Replacer().addPlayer((Player) sender).replace(mcMsg);

				if (mcMsg == null || mcMsg.isEmpty()) return;
				send(sender, Level.INFO, mcMsg);
			} else {
				BungeeSpeak.setMuted((Player) sender, true);

				String mcMsg = Messages.MC_COMMAND_MUTE.get();
				mcMsg = new Replacer().addPlayer((Player) sender).replace(mcMsg);

				if (mcMsg == null || mcMsg.isEmpty()) return;
				send(sender, Level.INFO, mcMsg);
			}
		} else {
			send(sender, Level.INFO, "Can only mute BungeeSpeak for players!");
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
