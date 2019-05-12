package de.redstoneworld.bungeespeak.teamspeakEvent;

import java.util.Map;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Messages;

public class LeaveEvent extends TeamspeakEvent {

	private Map<String, String> info;

	public LeaveEvent(Map<String, String> infoMap) {
		int clid = Integer.parseInt(infoMap.get("clid"));

		setUser(clid);
		BungeeSpeak.getClientList().removeClient(Integer.parseInt(infoMap.get("clid")));
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
