package de.redstoneworld.bungeespeak.Commands;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.util.DateManager;

import net.md_5.bungee.api.CommandSender;

public class CommandStatus extends BungeeSpeakCommand {

	public CommandStatus() {
		super("status", "version");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		send(sender, Level.INFO, "&eBukkitSpeak Version: &av" + BungeeSpeak.getInstance().getDescription().getVersion());
		if (BungeeSpeak.getQuery().isConnected()) {
			send(sender, Level.INFO, "&eTeamspeak Listener: &arunning");
			send(sender, Level.INFO, "&eRunning since: &a"
					+ DateManager.dateToString(BungeeSpeak.getInstance().getStartedTime()));
			send(sender, Level.INFO, "&eSID = &a" + BungeeSpeak.getQueryInfo().getVirtualServerId()
					+ "&e, CID = &a" + BungeeSpeak.getQueryInfo().getChannelId() + "&e, CLID = &a"
					+ BungeeSpeak.getQueryInfo().getId());
		} else if (BungeeSpeak.getInstance().getStoppedTime() == null
				|| BungeeSpeak.getInstance().getStartedTime() == null) {
			send(sender, Level.WARNING, "&eTeamspeak Listener: &6connecting");
			if (BungeeSpeak.getInstance().getStartedTime() != null) {
				send(sender, Level.WARNING, "&eConnecting since: &6"
						+ DateManager.dateToString(BungeeSpeak.getInstance().getStartedTime()));
			}
		} else if (BungeeSpeak.getInstance().getLastStartedTime() == null) {
			send(sender, Level.WARNING, "&eTeamspeak Listener: &4dead");
			send(sender, Level.WARNING, "&eListener started: &4"
					+ DateManager.dateToString(BungeeSpeak.getInstance().getStartedTime()));
			send(sender, Level.WARNING, "&eStopped since: &4"
					+ DateManager.dateToString(BungeeSpeak.getInstance().getStoppedTime()));
			send(sender, Level.WARNING, "&eUse &a/tsa reload &eto restart the listener!");
		} else if (BungeeSpeak.getInstance().getLastStoppedTime() == null) {
			send(sender, Level.WARNING, "&eTeamspeak Listener: &6reconnecting");
			send(sender, Level.WARNING, "&eListener started: &4"
					+ DateManager.dateToString(BungeeSpeak.getInstance().getStartedTime()));
			send(sender, Level.WARNING, "&eListener stopped: &4"
					+ DateManager.dateToString(BungeeSpeak.getInstance().getStoppedTime()));
			send(sender, Level.WARNING, "&eReconnecting since: &6"
					+ DateManager.dateToString(BungeeSpeak.getInstance().getLastStartedTime()));
			send(sender, Level.WARNING, "&eUse &a/tsa reload &eto restart the listener!");
		} else {
			send(sender, Level.WARNING, "&eTeamspeak Listener: &4dead");
			send(sender, Level.WARNING, "&eRunning since: &4"
					+ DateManager.dateToString(BungeeSpeak.getInstance().getStartedTime()));
			send(sender, Level.WARNING, "&eStopped since: &4"
					+ DateManager.dateToString(BungeeSpeak.getInstance().getStoppedTime()));
			send(sender, Level.WARNING, "&eLast reconnecting attempt: &4"
					+ DateManager.dateToString(BungeeSpeak.getInstance().getLastStartedTime()));
			send(sender, Level.WARNING, "&eReconnecting failed: &4"
					+ DateManager.dateToString(BungeeSpeak.getInstance().getLastStoppedTime()));
			send(sender, Level.WARNING, "&eUse &a/tsa reload &eto restart the listener!");
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}
}
