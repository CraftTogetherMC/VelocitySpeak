package de.redstoneworld.bungeespeak.teamspeakEvent;

import java.util.regex.Pattern;

import com.github.theholywaffle.teamspeak3.api.event.*;
import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;

public class TeamspeakListener extends TS3EventAdapter {

	@Override
	public void onClientJoin(ClientJoinEvent event) {
		if (event.getClientType() != 0 || event.getClientNickname().startsWith("Unknown from")) return;
		new EnterEvent(event.getMap());
	}

	@Override
	public void onClientLeave(ClientLeaveEvent event) {
		if (!BungeeSpeak.getClientList().containsID(event.getClientId())) return;
		new LeaveEvent(event.getMap());
	}

	@Override
	public void onTextMessage(TextMessageEvent e) {
		String message = e.getMessage();

		String reg = Pattern.quote(Configuration.TS_COMMANDS_PREFIX.getString());
		if (Configuration.TS_COMMANDS_ENABLED.getBoolean() && message.matches(reg + "\\S.*")) {
			new TeamspeakCommandEvent(e.getMap());
		} else {
			new ServerMessageEvent(e.getMap());
		}
	}

	@Override
	public void onClientMoved(com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent e) {
		if (e.getClientId() == BungeeSpeak.getQueryInfo().getId()) {
			BungeeSpeak.updateQueryInfo();
		}
		new ClientMovedEvent(e.getMap());
	}

	@Override
	public void onChannelDeleted(ChannelDeletedEvent event) {
		if (event.getChannelId() == BungeeSpeak.getQueryInfo().getChannelId()) {
			BungeeSpeak.updateQueryInfo();
		}
	}
}
