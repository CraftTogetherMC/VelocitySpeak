package de.crafttogether.velocityspeak.teamspeakEvent;

import java.util.Map;

import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.VelocitySpeak;

public class LeaveEvent extends TeamspeakEvent {

	private Map<String, String> info;

	public LeaveEvent(Map<String, String> infoMap) {
		int clid = Integer.parseInt(infoMap.get("clid"));

		setUser(clid);
		VelocitySpeak.getClientList().removeClient(Integer.parseInt(infoMap.get("clid")));
		info = infoMap;
		performAction();
	}

	@Override
	protected void performAction() {
		if (getUser() == null || getClientName().startsWith("Unknown from") || getClientType() != 0) return;
		if (info.get("reasonid").equals("5")) return;

		sendMessage(Messages.TS_EVENT_SERVER_QUIT, "leave");
	}
}
