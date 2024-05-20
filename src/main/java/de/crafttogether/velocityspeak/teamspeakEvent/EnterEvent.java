package de.crafttogether.velocityspeak.teamspeakEvent;

import java.util.Map;

import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.ClientList;

public class EnterEvent extends TeamspeakEvent {

	public EnterEvent(Map<String, String> infoMap) {
		int clid = Integer.valueOf(infoMap.get("clid"));
		ClientList clientList = VelocitySpeak.getClientList();

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
