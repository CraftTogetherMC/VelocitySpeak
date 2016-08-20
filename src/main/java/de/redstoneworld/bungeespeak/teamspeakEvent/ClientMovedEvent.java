package de.redstoneworld.bungeespeak.teamspeakEvent;

import java.util.HashMap;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Messages;

public class ClientMovedEvent extends TeamspeakEvent {

	private HashMap<String, String> info;

	public ClientMovedEvent(HashMap<String, String> infoMap) {
		setUser(Integer.parseInt(infoMap.get("clid")));
		info = infoMap;
		BungeeSpeak.getClientList().asyncUpdateClient(Integer.parseInt(infoMap.get("clid")));

		if (getUser() == null) return;
		getUser().put("cid", infoMap.get("ctid"));
		performAction();
	}

	@Override
	protected void performAction() {
		if (getClientName().startsWith("Unknown from") || getClientType() != 0) return;

		if (info.get("reasonid").equals("4")) return;

		if (Integer.parseInt(info.get("ctid")) == BungeeSpeak.getQuery().getCurrentQueryClientChannelID()) {
			// Client entered channel
			sendMessage(Messages.TS_EVENT_CHANNEL_ENTER, "channelenter");
		} else {
			// Client left channel
			sendMessage(Messages.TS_EVENT_CHANNEL_LEAVE, "channelleave");
		}
	}
}
