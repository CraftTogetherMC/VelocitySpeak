package de.crafttogether.velocityspeak.teamspeakEvent;

import java.util.Map;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.AsyncQueryUtils.QuerySender;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.util.Replacer;

import de.crafttogether.velocityspeak.util.MessageUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ServerMessageEvent extends TeamspeakEvent {

	private Map<String, String> info;

	public ServerMessageEvent(Map<String, String> infoMap) {
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

			for (Player pl : VelocitySpeak.getInstance().getProxy().getAllPlayers()) {
				if (!VelocitySpeak.getMuted(pl) && checkPermissions(pl, "broadcast")) {
					pl.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(m));
				}
			}
			if (Configuration.TS_LOGGING.getBoolean()) {
				m = MessageUtil.toMinecraft(m, false, true);
				VelocitySpeak.log().info(m);
			}

		} else if (info.get("targetmode").equals("2")) {
			sendMessage(Messages.TS_EVENT_CHANNEL_MESSAGE, "chat", msg);
		} else if (info.get("targetmode").equals("1")) {
			String m = Messages.TS_EVENT_PRIVATE_MESSAGE.get();
			if (m.isEmpty()) return;
			m = new Replacer().addClient(getUser()).addMessage(msg).replace(m);
			m = MessageUtil.toMinecraft(m, true, true);

			String p = VelocitySpeak.getInstance().getRecipient(getClientId());
			if (p == null || p.isEmpty()) {
				String tsMsg = Messages.TS_EVENT_PRIVATE_MESSAGE_NO_CONVERSATION.get();
				Replacer r = new Replacer().addClient(getUser()).addMessage(msg);
				tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, true);

				if (tsMsg == null || tsMsg.isEmpty()) return;
				QuerySender qs = new QuerySender(getClientId(), TextMessageTargetMode.CLIENT, tsMsg);
				VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
				return;
			}

			if (MessageUtil.toMinecraft(Configuration.TS_CONSOLE_NAME.getString(), false, false).equals(p)) {
				VelocitySpeak.log().info(MessageUtil.toMinecraft(m, false, Configuration.TS_ALLOW_LINKS.getBoolean()));
			} else {
				Player pl = VelocitySpeak.getInstance().getProxy().getPlayer(p).get();
				if (!VelocitySpeak.getInstance().getProxy().getPlayer(p).isPresent()) {
					String tsMsg = Messages.TS_EVENT_PRIVATE_MESSAGE_RECIPIENT_OFFLINE.get();
					Replacer r = new Replacer().addTargetClient(getUser());
					tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, true);

					if (tsMsg == null || tsMsg.isEmpty()) return;
					QuerySender qs = new QuerySender(getClientId(), TextMessageTargetMode.CLIENT, tsMsg);
					VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
					return;
				} else if (VelocitySpeak.getMuted(pl) || !checkPermissions(pl, "pm")) {
					String tsMsg = Messages.TS_EVENT_PRIVATE_MESSAGE_RECIPIENT_MUTED.get();
					Replacer r = new Replacer().addTargetClient(getUser()).addPlayer(pl);
					tsMsg = MessageUtil.toTeamspeak(r.replace(tsMsg), true, true);

					if (tsMsg == null || tsMsg.isEmpty()) return;
					QuerySender qs = new QuerySender(getClientId(), TextMessageTargetMode.CLIENT, tsMsg);
					VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
					return;
				}

				pl.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(m));
			}
		}
	}
}
