package de.crafttogether.velocityspeak.teamspeakEvent;

import java.util.Map;

import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.util.MessageUtil;
import de.crafttogether.velocityspeak.util.Replacer;
import net.kyori.adventure.text.Component;

public abstract class TeamspeakEvent {

	private Map<String, String> user;

	public Map<String, String> getUser() {
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
		user = VelocitySpeak.getClientList().get(clid).getMap();
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
		for (Player pl : VelocitySpeak.getInstance().getProxy().getAllPlayers()) {
			if (!VelocitySpeak.getMuted(pl) && checkPermissions(pl, permission)) {
				pl.sendMessage(Component.text(m));
			}
		}

		// Finally log in console if enabled
		if (Configuration.TS_LOGGING.getBoolean()) {
			m = MessageUtil.toMinecraft(m, false, true);
			VelocitySpeak.log().info(m);
		}
	}

	protected boolean checkPermissions(Player player, String perm) {
		return player.hasPermission("bungeespeak.messages." + perm);
	}

	protected abstract void performAction();
}
