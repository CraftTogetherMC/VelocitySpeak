package de.redstoneworld.bungeespeak.AsyncQueryUtils;

import de.redstoneworld.bungeespeak.BungeeSpeak;

public class QuerySender implements Runnable {

	private int id, mode;
	private String msg;

	public QuerySender(int targetID, int targetMode, String message) {
		id = targetID;
		mode = targetMode;
		msg = message;
	}

	@Override
	public void run() {
		BungeeSpeak.getQuery().sendTextMessage(id, mode, msg);
	}
}
