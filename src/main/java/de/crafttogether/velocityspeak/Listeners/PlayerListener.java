package de.crafttogether.velocityspeak.Listeners;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.TsTarget;
import de.crafttogether.velocityspeak.util.Replacer;
import de.crafttogether.velocityspeak.AsyncQueryUtils.QuerySender;
import de.crafttogether.velocityspeak.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class PlayerListener {

	@Subscribe(order = PostOrder.LAST)
	public void onPlayerJoin(ServerConnectedEvent e) {
		if (!VelocitySpeak.getInstance().isEnabled()) return;
		//if (e.getPlayer().getServer() != null) return; // TODO: Check
		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.NONE) return;

		if (!hasPermission(e.getPlayer(), "join")) return;

		String tsMsg = Messages.MC_EVENT_LOGIN.get();
		tsMsg = new Replacer().addPlayer(e.getPlayer()).replace(tsMsg);
		tsMsg = MessageUtil.toTeamspeak(tsMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean());

		if (tsMsg.isEmpty()) return;

		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.CHANNEL) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getChannelId(),
					TextMessageTargetMode.CHANNEL, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
		} else if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.SERVER) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getVirtualServerId(),
					TextMessageTargetMode.SERVER, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
		}
	}

	@Subscribe(order = PostOrder.LAST)
	public void onPlayerQuit(DisconnectEvent e) {
		if (!VelocitySpeak.getInstance().isEnabled()) return;
		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.NONE) return;
		if (e.getPlayer() == null) return;

		if (!hasPermission(e.getPlayer(), "quit")) return;

		String tsMsg = Messages.MC_EVENT_LOGOUT.get();
		tsMsg = new Replacer().addPlayer(e.getPlayer()).replace(tsMsg);
		tsMsg = MessageUtil.toTeamspeak(tsMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean());

		if (tsMsg.isEmpty()) return;

		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.CHANNEL) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getChannelId(),
					TextMessageTargetMode.CHANNEL, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
		} else if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.SERVER) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getVirtualServerId(),
					TextMessageTargetMode.SERVER, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
		}
	}

	@Subscribe(order = PostOrder.LAST)
	public void onPlayerKick(KickedFromServerEvent e) {
		if (!VelocitySpeak.getInstance().isEnabled()) return;


		//if (e.getCancelServer() != null || e.getState() != ServerKickEvent.State.CONNECTED) return; //TODO: Check this
		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.NONE) return;
		if (e.getPlayer() == null) return;

		String tsMsg;
		/* TODO: Support for ban plugins?
		if (e.getPlayer().isBanned()) {
			// Was banned
			if (!hasPermission(e.getPlayer(), "ban")) return;
			tsMsg = Messages.MC_EVENT_BAN.get();
		} else {*/
			// Or just kicked
			if (!hasPermission(e.getPlayer(), "kick")) return;
			tsMsg = Messages.MC_EVENT_KICK.get();
		//}

		tsMsg = new Replacer().addPlayer(e.getPlayer()).addMessage(LegacyComponentSerializer.legacyAmpersand().serialize(e.getServerKickReason().orElse(Component.text("")))).replace(tsMsg);
		tsMsg = MessageUtil.toTeamspeak(tsMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean());

		if (tsMsg.isEmpty()) return;

		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.CHANNEL) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getChannelId(),
					TextMessageTargetMode.CHANNEL, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
		} else if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.SERVER) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getVirtualServerId(),
					TextMessageTargetMode.SERVER, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
		}
	}

	private boolean hasPermission(Player player, String perm) {
		return player.hasPermission("bungeespeak.sendteamspeak." + perm);
	}
}
