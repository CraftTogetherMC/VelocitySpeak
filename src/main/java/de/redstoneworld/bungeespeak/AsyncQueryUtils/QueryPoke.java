package de.redstoneworld.bungeespeak.AsyncQueryUtils;

import de.redstoneworld.bungeespeak.BungeeSpeak;

public class QueryPoke implements Runnable {

	private int id;
	private String msg;

	public QueryPoke(int clientID, String message) {
		id = clientID;
		msg = message;
	}

	@Override
	public void run() {
		BungeeSpeak.getQuery().pokeClient(id, msg);
	}
}
