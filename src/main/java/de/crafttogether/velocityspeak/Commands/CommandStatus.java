package de.crafttogether.velocityspeak.Commands;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.Plugin;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.util.DateManager;

public class CommandStatus extends BungeeSpeakCommand {

	public CommandStatus() {
		super("status", "version");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		send(source, Level.INFO, "&eBukkitSpeak Version: &av" + VelocitySpeak.getInstance().getClass().getAnnotation(Plugin.class).version());
		if (VelocitySpeak.getQuery().isConnected()) {
			send(source, Level.INFO, "&eTeamspeak Listener: &arunning");
			send(source, Level.INFO, "&eRunning since: &a"
					+ DateManager.dateToString(VelocitySpeak.getInstance().getStartedTime()));
			send(source, Level.INFO, "&eSID = &a" + VelocitySpeak.getQueryInfo().getVirtualServerId()
					+ "&e, CID = &a" + VelocitySpeak.getQueryInfo().getChannelId() + "&e, CLID = &a"
					+ VelocitySpeak.getQueryInfo().getId());
		} else if (VelocitySpeak.getInstance().getStoppedTime() == null
				|| VelocitySpeak.getInstance().getStartedTime() == null) {
			send(source, Level.WARNING, "&eTeamspeak Listener: &6connecting");
			if (VelocitySpeak.getInstance().getStartedTime() != null) {
				send(source, Level.WARNING, "&eConnecting since: &6"
						+ DateManager.dateToString(VelocitySpeak.getInstance().getStartedTime()));
			}
		} else if (VelocitySpeak.getInstance().getLastStartedTime() == null) {
			send(source, Level.WARNING, "&eTeamspeak Listener: &4dead");
			send(source, Level.WARNING, "&eListener started: &4"
					+ DateManager.dateToString(VelocitySpeak.getInstance().getStartedTime()));
			send(source, Level.WARNING, "&eStopped since: &4"
					+ DateManager.dateToString(VelocitySpeak.getInstance().getStoppedTime()));
			send(source, Level.WARNING, "&eUse &a/tsa reload &eto restart the listener!");
		} else if (VelocitySpeak.getInstance().getLastStoppedTime() == null) {
			send(source, Level.WARNING, "&eTeamspeak Listener: &6reconnecting");
			send(source, Level.WARNING, "&eListener started: &4"
					+ DateManager.dateToString(VelocitySpeak.getInstance().getStartedTime()));
			send(source, Level.WARNING, "&eListener stopped: &4"
					+ DateManager.dateToString(VelocitySpeak.getInstance().getStoppedTime()));
			send(source, Level.WARNING, "&eReconnecting since: &6"
					+ DateManager.dateToString(VelocitySpeak.getInstance().getLastStartedTime()));
			send(source, Level.WARNING, "&eUse &a/tsa reload &eto restart the listener!");
		} else {
			send(source, Level.WARNING, "&eTeamspeak Listener: &4dead");
			send(source, Level.WARNING, "&eRunning since: &4"
					+ DateManager.dateToString(VelocitySpeak.getInstance().getStartedTime()));
			send(source, Level.WARNING, "&eStopped since: &4"
					+ DateManager.dateToString(VelocitySpeak.getInstance().getStoppedTime()));
			send(source, Level.WARNING, "&eLast reconnecting attempt: &4"
					+ DateManager.dateToString(VelocitySpeak.getInstance().getLastStartedTime()));
			send(source, Level.WARNING, "&eReconnecting failed: &4"
					+ DateManager.dateToString(VelocitySpeak.getInstance().getLastStoppedTime()));
			send(source, Level.WARNING, "&eUse &a/tsa reload &eto restart the listener!");
		}
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		return Collections.emptyList();
	}
}
