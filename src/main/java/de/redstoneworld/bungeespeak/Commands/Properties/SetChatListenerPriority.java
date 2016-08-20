package de.redstoneworld.bungeespeak.Commands.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Listeners.ChatListener;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SetChatListenerPriority extends SetProperty {

	private static final Configuration PROPERTY = Configuration.TS_CHAT_LISTENER_PRIORITY;
	private static final String ALLOWED_INPUT = "LOWEST, LOW, NORMAL, HIGH, HIGHEST or MONITOR";
	private static final String DESCRIPTION = "The priority of the chat listener.";

	@Override
	public Configuration getProperty() {
		return PROPERTY;
	}

	@Override
	public String getAllowedInput() {
		return ALLOWED_INPUT;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public boolean execute(CommandSender sender, String arg) {
		EventPriority p = null;
		for (EventPriority val : EventPriority.values()) {
			if (val.name().equalsIgnoreCase(arg)) {
				p = val;
				break;
			}
		}
		if (p == null) {
			send(sender, Level.WARNING, "&4Only LOWEST, LOW, NORMAL, HIGH, HIGHEST or MONITOR are accepted.");
			return false;
		}

		PROPERTY.set(p.name());
		ChatListener cl = BungeeSpeak.getInstance().getChatListener();
		AsyncPlayerChatEvent.getHandlerList().unregister(cl);
		boolean i = (p != EventPriority.LOWEST);
		Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, cl, p, cl, BungeeSpeak.getInstance(), i);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length != 3) return null;
		List<String> al = new ArrayList<String>();
		for (EventPriority p : EventPriority.values()) {
			if (p.name().toLowerCase().startsWith(args[2].toLowerCase())) {
				al.add(p.name());
			}
		}
		return al;
	}
}
