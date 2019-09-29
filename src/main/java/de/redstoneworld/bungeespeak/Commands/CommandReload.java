package de.redstoneworld.bungeespeak.Commands;

import java.util.Collections;
import java.util.List;

import de.redstoneworld.bungeespeak.BungeeSpeak;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandReload extends BungeeSpeakCommand {

	public CommandReload() {
		super("reload");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		String t = BungeeSpeak.getInstance().toString();
		if (BungeeSpeak.getInstance().reload()) {
			if (sender instanceof ProxiedPlayer) {
				sender.sendMessage(t + ChatColor.GREEN + "reloaded.");
			}
		} else {
			if (sender instanceof ProxiedPlayer) {
				sender.sendMessage(t + ChatColor.RED + "was unable to reload, an error happened.");
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}
}
