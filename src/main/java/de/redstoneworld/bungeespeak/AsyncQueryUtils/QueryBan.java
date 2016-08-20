package de.redstoneworld.bungeespeak.AsyncQueryUtils;

import java.util.HashMap;

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

		HashMap<String, String> hmIn;
		StringBuilder command = new StringBuilder().append("banclient");
		command.append(" clid=").append(String.valueOf(id));
		if (reason != null && !reason.isEmpty()) {
			command.append(" banreason=").append(BungeeSpeak.getQuery().encodeTS3String(reason));
		}

		try {
			hmIn = BungeeSpeak.getQuery().doCommand(command.toString());

			if (!hmIn.get("id").equals("0")) {
				BungeeSpeak.log().info("banClient()" + hmIn.get("id") + hmIn.get("msg") + hmIn.get("extra_msg")
						+ hmIn.get("failed_permid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
