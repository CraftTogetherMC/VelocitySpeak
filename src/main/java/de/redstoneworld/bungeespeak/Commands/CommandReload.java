package de.redstoneworld.bungeespeak.Commands;

import java.util.List;

import de.redstoneworld.bungeespeak.BungeeSpeak;

import org.bukkit.ChatColor;
import net.md_5.bungee.api.CommandSender;
import org.bukkit.entity.Player;

public class CommandReload extends BungeeSpeakCommand {

	public CommandReload() {
		super("reload");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		String t = BungeeSpeak.getInstance().toString();
		if (BungeeSpeak.getInstance().reload()) {
			if (sender instanceof Player) {
				sender.sendMessage(t + ChatColor.GREEN + "reloaded.");
			}
		} else {
			if (sender instanceof Player) {
				sender.sendMessage(t + ChatColor.RED + "was unable to reload, an error happened.");
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
