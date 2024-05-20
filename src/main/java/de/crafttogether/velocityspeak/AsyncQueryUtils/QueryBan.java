package de.crafttogether.velocityspeak.AsyncQueryUtils;

import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import de.crafttogether.velocityspeak.VelocitySpeak;

public class QueryBan implements Runnable {

	private int id;
	private String reason;

	public QueryBan(int clientID, String banReason) {
		id = clientID;
		reason = banReason;
	}

	@Override
	public void run() {
		if (!VelocitySpeak.getQuery().isConnected()) {
			VelocitySpeak.log().warning("banClient(): Not connected to TS3 server!");
			return;
		}

		if (id <= 0) {
			VelocitySpeak.log().warning("banClient(): Client ID must be greater than 0!");
			return;
		}

		try {
			VelocitySpeak.getQuery().getApi().banClient(id, reason);
		} catch (TS3CommandFailedException ex) {
			VelocitySpeak.log().info("banClient()" + ex.getError().getId() + ex.getError().getMessage() + ex.getError().getExtraMessage() + ex.getError().getFailedPermissionId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
