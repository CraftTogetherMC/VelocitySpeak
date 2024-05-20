package de.crafttogether.velocityspeak.Listeners;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.TsTarget;
import de.crafttogether.velocityspeak.util.Replacer;
import de.crafttogether.velocityspeak.AsyncQueryUtils.QuerySender;
import de.crafttogether.velocityspeak.util.MessageUtil;

public class ChatListener {

	private Priority priority;

	public ChatListener(Priority priority) {
		setPriority(priority);
	}

	@Subscribe(order = PostOrder.LAST)
	public void onChatHighest(PlayerChatEvent e) {
		handle(e, Priority.HIGHEST);
	}

	@Subscribe(order = PostOrder.LATE)
	public void onChatHigh(PlayerChatEvent e) {
		handle(e, Priority.HIGH);
	}

	@Subscribe(order = PostOrder.NORMAL)
	public void onChatNormal(PlayerChatEvent e) {
		handle(e, Priority.NORMAL);
	}

	@Subscribe(order = PostOrder.EARLY)
	public void onChatLow(PlayerChatEvent e) {
		handle(e, Priority.LOW);
	}

	@Subscribe(order = PostOrder.FIRST)
	public void onChatLowest(PlayerChatEvent e) {
		handle(e, Priority.LOWEST);
	}

	public void handle(PlayerChatEvent e, Priority priority) {
		if (priority != this.priority) return;
		if (!VelocitySpeak.getInstance().isEnabled()) return;
		if (Configuration.TS_MESSAGES_TARGET.getTeamspeakTarget() == TsTarget.NONE) return;
		if (e.getMessage().isEmpty()) return;
		if (e.getMessage().startsWith("/")) return;

		/* If all players on the server will receive this message, it should be considered safe to relay */
		/* TODO: Find good implementation on Bungee, probably needs to depend on specific chat plugin?
		if (Configuration.PLUGINS_CHAT_RECIPIENTS_MUST_BE_EVERYONE.getBoolean()) {
			if (e.getRecipients().size() != Bukkit.getOnlinePlayers().size()) {
				return;
			}
		}*/

		if (!hasPermission(e.getPlayer(), "chat")) return;

		String tsMsg = Messages.MC_EVENT_CHAT.get();
		tsMsg = new Replacer().addPlayer(e.getPlayer()).addMessage(e.getMessage()).replace(tsMsg);
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

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public enum Priority {
		LOWEST(PostOrder.FIRST),
		LOW(PostOrder.EARLY),
		NORMAL(PostOrder.NORMAL),
		HIGH(PostOrder.LATE),
		HIGHEST(PostOrder.LAST);

		public final PostOrder eventPriority;

		private Priority(final PostOrder eventPriority) {
			this.eventPriority = eventPriority;
		}

		public int getEventPriority() {
			return eventPriority.ordinal();
		}
	}
}
