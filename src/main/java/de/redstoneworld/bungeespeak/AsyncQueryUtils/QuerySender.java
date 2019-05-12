package de.redstoneworld.bungeespeak.AsyncQueryUtils;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import de.redstoneworld.bungeespeak.BungeeSpeak;

public class QuerySender implements Runnable {

	private int id;
	private TextMessageTargetMode mode;
	private String msg;

	public QuerySender(int targetID, TextMessageTargetMode targetMode, String message) {
		id = targetID;
		mode = targetMode;
		msg = message;
	}

	@Override
	public void run() {
		BungeeSpeak.getQuery().getApi().sendTextMessage(mode, id, msg);
	}
}
