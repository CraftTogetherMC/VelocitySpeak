package de.redstoneworld.bungeespeak.Listeners;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.TsTarget;
import de.redstoneworld.bungeespeak.util.Replacer;
import de.redstoneworld.bungeespeak.AsyncQueryUtils.QuerySender;
import de.redstoneworld.bungeespeak.util.MessageUtil;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ChatListener implements Listener {

	private Priority priority;

	public ChatListener(Priority priority) {
		setPriority(priority);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChatHighest(ChatEvent e) {
		handle(e, Priority.HIGHEST);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onChatHigh(ChatEvent e) {
		handle(e, Priority.HIGH);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onChatNormal(ChatEvent e) {
		handle(e, Priority.NORMAL);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onChatLow(ChatEvent e) {
		handle(e, Priority.LOW);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChatLowest(ChatEvent e) {
		handle(e, Priority.LOWEST);
	}

	public void handle(ChatEvent e, Priority priority) {
		if (priority != this.priority) return;
		if (!BungeeSpeak.getInstance().isEnabled()) return;
		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.NONE) return;
		if (e.getSender() == null || !(e.getSender() instanceof ProxiedPlayer) || e.getMessage().isEmpty()) return;
		if (e.getMessage().startsWith("/")) return;

		/* If all players on the server will receive this message, it should be considered safe to relay */
		/* TODO: Find good implementation on Bungee
		if (Configuration.PLUGINS_CHAT_RECIPIENTS_MUST_BE_EVERYONE.getBoolean()) {
			if (e.getRecipients().size() != Bukkit.getOnlinePlayers().size()) {
				return;
			}
		}*/

		if (!hasPermission((ProxiedPlayer) e.getSender(), "chat")) return;

		String tsMsg = Messages.MC_EVENT_CHAT.get();
		tsMsg = new Replacer().addPlayer((ProxiedPlayer) e.getSender()).addMessage(e.getMessage()).replace(tsMsg);
		tsMsg = MessageUtil.toTeamspeak(tsMsg, true, Configuration.TS_ALLOW_LINKS.getBoolean());

		if (tsMsg.isEmpty()) return;

		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.CHANNEL) {
			QuerySender qs = new QuerySender(BungeeSpeak.getQuery().getCurrentQueryClientChannelID(),
					JTS3ServerQuery.TEXTMESSAGE_TARGET_CHANNEL, tsMsg);
			BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), qs);
		} else if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.SERVER) {
			QuerySender qs = new QuerySender(BungeeSpeak.getQuery().getCurrentQueryClientServerID(),
					JTS3ServerQuery.TEXTMESSAGE_TARGET_VIRTUALSERVER, tsMsg);
			BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), qs);
		}
	}

	private boolean hasPermission(ProxiedPlayer player, String perm) {
		return player.hasPermission("bungeespeak.sendteamspeak." + perm);
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public enum Priority {
		LOWEST(EventPriority.LOWEST),
		LOW(EventPriority.LOW),
		NORMAL(EventPriority.NORMAL),
		HIGH(EventPriority.HIGH),
		HIGHEST(EventPriority.HIGHEST);

		public final byte eventPriority;

		private Priority(final byte eventPriority) {
			this.eventPriority = eventPriority;
		}

		public int getEventPriority() {
			return eventPriority;
		}
	}
}
