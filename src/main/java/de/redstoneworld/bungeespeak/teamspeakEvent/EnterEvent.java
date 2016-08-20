package de.redstoneworld.bungeespeak.teamspeakEvent;

import java.util.HashMap;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.ClientList;
import de.redstoneworld.bungeespeak.Configuration.Messages;

public class EnterEvent extends TeamspeakEvent {

	public EnterEvent(HashMap<String, String> infoMap) {
		int clid = Integer.valueOf(infoMap.get("clid"));
		ClientList clientList = BungeeSpeak.getClientList();

		if (clientList.containsID(clid)) return;
		clientList.updateClient(clid);

		if (clientList.containsID(clid)) {
			setUser(clid);
			performAction();
		}
	}

	protected void performAction() {
		sendMessage(Messages.TS_EVENT_SERVER_JOIN, "join");
	}
}
