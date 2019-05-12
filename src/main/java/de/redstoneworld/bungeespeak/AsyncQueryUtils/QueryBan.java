package de.redstoneworld.bungeespeak.AsyncQueryUtils;

import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import de.redstoneworld.bungeespeak.BungeeSpeak;

public class QueryBan implements Runnable {

	private int id;
	private String reason;

	public QueryBan(int clientID, String banReason) {
		id = clientID;
		reason = banReason;
	}

	@Override
	public void run() {
		if (!BungeeSpeak.getQuery().isConnected()) {
			BungeeSpeak.log().warning("banClient(): Not connected to TS3 server!");
			return;
		}

		if (id <= 0) {
			BungeeSpeak.log().warning("banClient(): Client ID must be greater than 0!");
			return;
		}

		try {
			BungeeSpeak.getQuery().getApi().banClient(id, reason);
		} catch (TS3CommandFailedException ex) {
			BungeeSpeak.log().info("banClient()" + ex.getError().getId() + ex.getError().getMessage() + ex.getError().getExtraMessage() + ex.getError().getFailedPermissionId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
