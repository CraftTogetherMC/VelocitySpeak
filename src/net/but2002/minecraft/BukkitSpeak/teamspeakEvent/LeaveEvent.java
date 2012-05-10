package net.but2002.minecraft.BukkitSpeak.teamspeakEvent;

import java.util.HashMap;

import org.bukkit.entity.Player;

import net.but2002.minecraft.BukkitSpeak.BukkitSpeak;

public class LeaveEvent extends TeamspeakEvent{
	
	public LeaveEvent(BukkitSpeak plugin, HashMap<String, String> info) {
		super(plugin, Integer.parseInt(info.get("clid")));
		sendMessage();
	}
	
	@Override
	protected void sendMessage() {
		if(user != null && !getClientName().startsWith("Unknown from") && getClientType() == 0) {
			String m = plugin.getStringManager().getMessage("Quit");
			for (Player pl : plugin.getServer().getOnlinePlayers()) {
				if (!plugin.getMuted(pl) && CheckPermissions(pl, "leave")) {
					pl.sendMessage(replaceValues(m, true));
				}
			}
			plugin.getLogger().info(replaceValues(m, false));
		}
	}
}
