package de.crafttogether.velocityspeak.teamspeakEvent;

import java.util.Map;

import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Messages;

public class ClientMovedEvent extends TeamspeakEvent {

	private Map<String, String> info;

	public ClientMovedEvent(Map<String, String> infoMap) {
		setUser(Integer.parseInt(infoMap.get("clid")));
		info = infoMap;
		VelocitySpeak.getClientList().asyncUpdateClient(Integer.parseInt(infoMap.get("clid")));

		if (getUser() == null) return;
		getUser().put("cid", infoMap.get("ctid"));
		performAction();
	}

	@Override
	protected void performAction() {
		if (getClientName().startsWith("Unknown from") || getClientType() != 0) return;

		if (info.get("reasonid").equals("4")) return;

		if (Integer.parseInt(info.get("ctid")) == VelocitySpeak.getQueryInfo().getChannelId()) {
			// Client entered channel
			sendMessage(Messages.TS_EVENT_CHANNEL_ENTER, "channelenter");
		} else {
			// Client left channel
			sendMessage(Messages.TS_EVENT_CHANNEL_LEAVE, "channelleave");
		}
	}
}
