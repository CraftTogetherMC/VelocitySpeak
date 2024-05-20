package de.crafttogether.velocityspeak.AsyncQueryUtils;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import de.crafttogether.velocityspeak.VelocitySpeak;

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
		VelocitySpeak.getQuery().getApi().sendTextMessage(mode, id, msg);
	}
}
