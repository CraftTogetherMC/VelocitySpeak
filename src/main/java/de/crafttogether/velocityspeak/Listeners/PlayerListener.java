package de.crafttogether.velocityspeak.Listeners;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.TsTarget;
import de.crafttogether.velocityspeak.util.Replacer;
import de.crafttogether.velocityspeak.AsyncQueryUtils.QuerySender;
import de.crafttogether.velocityspeak.util.MessageUtil;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(ServerConnectEvent e) {
		if (!VelocitySpeak.getInstance().isEnabled()) return;
		if (e.getPlayer().getServer() != null) return;
		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.NONE) return;

		if (!hasPermission(e.getPlayer(), "join")) return;

		String tsMsg = Messages.MC_EVENT_LOGIN.get();
		tsMsg = new Replacer().addPlayer(e.getPlayer()).replace(tsMsg);
		tsMsg = MessageUtil.toTeamspeak(tsMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean());

		if (tsMsg.isEmpty()) return;

		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.CHANNEL) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getChannelId(),
					TextMessageTargetMode.CHANNEL, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().runAsync(VelocitySpeak.getInstance(), qs);
		} else if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.SERVER) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getVirtualServerId(),
					TextMessageTargetMode.SERVER, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().runAsync(VelocitySpeak.getInstance(), qs);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerDisconnectEvent e) {
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
			VelocitySpeak.getInstance().getProxy().getScheduler().runAsync(VelocitySpeak.getInstance(), qs);
		} else if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.SERVER) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getVirtualServerId(),
					TextMessageTargetMode.SERVER, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().runAsync(VelocitySpeak.getInstance(), qs);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(ServerKickEvent e) {
		if (!VelocitySpeak.getInstance().isEnabled()) return;
		if (e.isCancelled()) return;

		if (e.getCancelServer() != null || e.getState() != ServerKickEvent.State.CONNECTED) return;
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

		tsMsg = new Replacer().addPlayer(e.getPlayer()).addMessage(TextComponent.toLegacyText(e.getKickReasonComponent())).replace(tsMsg);
		tsMsg = MessageUtil.toTeamspeak(tsMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean());

		if (tsMsg.isEmpty()) return;

		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.CHANNEL) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getChannelId(),
					TextMessageTargetMode.CHANNEL, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().runAsync(VelocitySpeak.getInstance(), qs);
		} else if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.SERVER) {
			QuerySender qs = new QuerySender(VelocitySpeak.getQueryInfo().getVirtualServerId(),
					TextMessageTargetMode.SERVER, tsMsg);
			VelocitySpeak.getInstance().getProxy().getScheduler().runAsync(VelocitySpeak.getInstance(), qs);
		}
	}

	private boolean hasPermission(ProxiedPlayer player, String perm) {
		return player.hasPermission("bungeespeak.sendteamspeak." + perm);
	}
}
