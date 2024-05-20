package de.crafttogether.velocityspeak.Commands;

import java.util.Collections;
import java.util.List;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.VelocitySpeak;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CommandReload extends BungeeSpeakCommand {

	public CommandReload() {
		super("reload");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		String t = VelocitySpeak.getInstance().toString();
		if (VelocitySpeak.getInstance().reload()) {
			if (source instanceof Player) {
				source.sendMessage(
						Component.text(t).color(NamedTextColor.WHITE)
								.append(Component.text(" reloaded.").color(NamedTextColor.GREEN)));
			}
		} else {
			if (source instanceof Player) {
				source.sendMessage(
						Component.text(t).color(NamedTextColor.WHITE)
								.append(Component.text(" was unable to reload, an error happened..").color(NamedTextColor.RED)));
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		return Collections.emptyList();
	}
}
