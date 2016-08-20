package de.redstoneworld.bungeespeak.teamspeakEvent;

import java.util.HashMap;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.Replacer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;
import de.redstoneworld.bungeespeak.AsyncQueryUtils.QuerySender;
import de.redstoneworld.bungeespeak.util.MessageUtil;

public class ServerMessageEvent extends TeamspeakEvent {

	private HashMap<String, String> info;

	public ServerMessageEvent(HashMap<String, String> infoMap) {
		setUser(Integer.parseInt(infoMap.get("invokerid")));
		info = infoMap;

		if (getUser() == null) return;
		performAction();
	}

	@Override
	protected void performAction() {
		if (info == null || getClientType() != 0) return;

		String msg = info.get("msg");
		msg = msg.replaceAll("\\n", " ");
		msg = MessageUtil.toMinecraft(msg, true, Configuration.TS_ALLOW_LINKS.getBoolean());
		if (msg.isEmpty()) return;

		if (info.get("targetmode").equals("3")) {
			String m = Messages.TS_EVENT_SERVER_MESSAGE.get();
			if (m.isEmpty()) return;
			m = new Replacer().addClient(getUser()).addMessage(msg).replace(m);
			m = MessageUtil.toMinecraft(m, true, true);

			for (ProxiedPlayer pl : BungeeSpeak.getInstance().getProxy().getPlayers()) {
				if (!BungeeSpeak.getMuted(pl) && checkPermissions(pl, "broadcast")) {
					pl.sendMessage(m);
				}
			}
			if (Configuration.TS_LOGGING.getBoolean()) {
				m = MessageUtil.toMinecraft(m, false, true);
				BungeeSpeak.log().info(m);
			}

		} else if (info.get("targetmode").equals("2")) {
			sendMessage(Messages.TS_EVENT_CHANNEL_MESSAGE, "chat", msg);
		} else if (info.get("targetmode").equals("1")) {
			String m = Messages.TS_EVENT_PRIVATE_MESSAGE.get();
			if (m.isEmpty()) return;
			m = new Replacer().addClient(getUser()).addMessage(msg).replace(m);
			m = MessageUtil.toMinecraft(m, true, true);

			String p = BungeeSpeak.getInstance().getRecipient(getClientId());
			if (p == null || p.isEmpty()) {
				String tsMsg = Messages.TS_EVENT_PRIVATE_MESSAGE_NO_CONVERSATION.get();
				Replacer r = new Replacer().addClient(getUser()).addMessage(msg);
				tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, true);

				if (tsMsg == null || tsMsg.isEmpty()) return;
				QuerySender qs = new QuerySender(getClientId(), JTS3ServerQuery.TEXTMESSAGE_TARGET_CLIENT, tsMsg);
				BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), qs);
				return;
			}

			if (MessageUtil.toMinecraft(Configuration.TS_CONSOLE_NAME.getString(), false, false).equals(p)) {
				BungeeSpeak.log().info(MessageUtil.toMinecraft(m, false, Configuration.TS_ALLOW_LINKS.getBoolean()));
			} else {
				@SuppressWarnings("deprecation")
				ProxiedPlayer pl = BungeeSpeak.getInstance().getProxy().getPlayer(p);
				if (pl == null) {
					String tsMsg = Messages.TS_EVENT_PRIVATE_MESSAGE_RECIPIENT_OFFLINE.get();
					Replacer r = new Replacer().addTargetClient(getUser());
					tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, true);

					if (tsMsg == null || tsMsg.isEmpty()) return;
					QuerySender qs = new QuerySender(getClientId(), JTS3ServerQuery.TEXTMESSAGE_TARGET_CLIENT, tsMsg);
					BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), qs);
					return;
				} else if (BungeeSpeak.getMuted(pl) || !checkPermissions(pl, "pm")) {
					String tsMsg = Messages.TS_EVENT_PRIVATE_MESSAGE_RECIPIENT_MUTED.get();
					Replacer r = new Replacer().addTargetClient(getUser()).addPlayer(pl);
					tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, true);

					if (tsMsg == null || tsMsg.isEmpty()) return;
					QuerySender qs = new QuerySender(getClientId(), JTS3ServerQuery.TEXTMESSAGE_TARGET_CLIENT, tsMsg);
					BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), qs);
					return;
				}

				pl.sendMessage(m);
			}
		}
	}
}
