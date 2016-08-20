package de.redstoneworld.bungeespeak.AsyncQueryUtils;

import de.redstoneworld.bungeespeak.BungeeSpeak;

public class QueryKick implements Runnable {

	private int id;
	private boolean local;
	private String reason;

	public QueryKick(int clientID, boolean onlyChannelKick, String kickReason) {
		id = clientID;
		local = onlyChannelKick;
		reason = kickReason;
	}

	@Override
	public void run() {
		BungeeSpeak.getQuery().kickClient(id, local, reason);
	}
}
