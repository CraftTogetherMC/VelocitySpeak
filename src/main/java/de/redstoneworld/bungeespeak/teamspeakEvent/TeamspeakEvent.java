package de.redstoneworld.bungeespeak.teamspeakEvent;

import java.util.HashMap;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.MessageUtil;
import de.redstoneworld.bungeespeak.util.Replacer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class TeamspeakEvent {

	private HashMap<String, String> user;

	public HashMap<String, String> getUser() {
		return user;
	}

	public String getClientName() {
		return user.get("client_nickname");
	}

	public Integer getClientId() {
		return Integer.valueOf(user.get("clid"));
	}

	public Integer getClientType() {
		return Integer.valueOf(user.get("client_type"));
	}

	protected void setUser(Integer clid) {
		user = BungeeSpeak.getClientList().get(clid);
	}

	protected void sendMessage(Messages message, String permission) {
		sendMessage(message, permission, null);
	}

	protected void sendMessage(Messages message, String permission, String msg) {
		String m = message.get();
		if (m.isEmpty()) return;
		Replacer r = new Replacer().addClient(getUser());
		if (msg != null && !msg.isEmpty()) {
			r.addMessage(msg);
		}
		m = MessageUtil.toMinecraft(r.replace(m), true, true);

		// Directly send to players with permissions
		for (ProxiedPlayer pl : BungeeSpeak.getInstance().getProxy().getPlayers()) {
			if (!BungeeSpeak.getMuted(pl) && checkPermissions(pl, permission)) {
				pl.sendMessage(m);
			}
		}

		// Finally log in console if enabled
		if (Configuration.TS_LOGGING.getBoolean()) {
			m = MessageUtil.toMinecraft(m, false, true);
			BungeeSpeak.log().info(m);
		}
	}

	protected boolean checkPermissions(ProxiedPlayer player, String perm) {
		return player.hasPermission("bungeespeak.messages." + perm);
	}

	protected abstract void performAction();
}
