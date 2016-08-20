package de.redstoneworld.bungeespeak.TeamspeakCommands;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.redstoneworld.bungeespeak.BungeeSpeak;
import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.redstoneworld.bungeespeak.Configuration.Messages;
import de.redstoneworld.bungeespeak.util.Replacer;
import de.redstoneworld.bungeespeak.AsyncQueryUtils.QuerySender;
import de.redstoneworld.bungeespeak.util.MessageUtil;

import net.md_5.bungee.api.CommandSender;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class TeamspeakCommandSender implements CommandSender {

	private final boolean operator;
	private final Map<String, Boolean> permissions;
	private final Map<String, String> client;
	private final String name;
	private final List<String> outBuffer;

	private BufferSender outSender;

	public TeamspeakCommandSender(Map<String, String> clientInfo, boolean op, Map<String, Boolean> perms) {
		client = clientInfo;
		name = replaceValues(Messages.TS_COMMAND_SENDER_NAME.get());
		outBuffer = Collections.synchronizedList(new LinkedList<String>());
		operator = op;

		permissions = new HashMap<String, Boolean>();
		for (Map.Entry<String, Boolean> e : perms.entrySet()) {
			permissions.put(e.getKey(), e.getValue());
		}
	}

	@Override
	public boolean hasPermission(String perm) {
		return permissions.containsKey(perm) && permissions.get(perm);
	}

	@Override
	public void setPermission(String s, boolean b) {
		permissions.put(s, b);
	}

	@Override
	public Collection<String> getPermissions() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	public Integer getClientID() {
		return Integer.valueOf(client.get("clid"));
	}

	public Map<String, String> getClientInfo() {
		return client;
	}

	@Override
	public void sendMessage(String message) {
		if (message == null) return;
		if (!BungeeSpeak.getQuery().isConnected()) return;

		outBuffer.add(format(message));
		startBuffer();
	}

	@Override
	public void sendMessages(String... strings) {
		if (strings == null || strings.length == 0) return;

		for (String string : strings) {
			outBuffer.add(format(string));
		}

		startBuffer();
	}

	@Override
	public void sendMessage(BaseComponent... baseComponents) {
		sendMessage(TextComponent.toLegacyText(baseComponents));
	}

	@Override
	public void sendMessage(BaseComponent baseComponent) {
		sendMessage(TextComponent.toLegacyText(baseComponent));
	}

	@Override
	public Collection<String> getGroups() {
		return new HashSet<String>();
	}

	@Override
	public void addGroups(String... strings) {

	}

	@Override
	public void removeGroups(String... strings) {

	}

	private String format(String s) {
		// TODO: Format message?
		return MessageUtil.toTeamspeak(s, true, true);
	}

	private void startBuffer() {
		if (outSender == null || outSender.isDone()) {
			outSender = new BufferSender(outBuffer, client);
			int buffer = Math.max(50, Configuration.TS_COMMANDS_MESSAGE_BUFFER.getInt());
			Executors.newSingleThreadScheduledExecutor().schedule(outSender, buffer, TimeUnit.MILLISECONDS);
		}
	}

	private String replaceValues(String input) {
		String output = MessageUtil.toMinecraft(input, true, false);
		output = new Replacer().addClient(client).replace(output);
		return output;
	}

	private class BufferSender implements Runnable {

		private static final int MSG_MAXLENGTH = 1024;

		private final List<String> buffer;
		private final int clid;

		private boolean done;

		public BufferSender(List<String> outBufferList, Map<String, String> clientInfo) {
			buffer = outBufferList;
			clid = Integer.valueOf(clientInfo.get("clid"));
		}

		public void run() {
			if (buffer.isEmpty()) return;
			StringBuilder sb = new StringBuilder(buffer.remove(0));

			for (String message : buffer) {
				if (sb.length() + message.length() + 2 < MSG_MAXLENGTH) {
					sb.append("\n").append(message);
				} else {
					sendToTeamspeak(sb.toString());
					sb = new StringBuilder(message);
				}
			}
			sendToTeamspeak(sb.toString());

			setDone();
		}

		public boolean isDone() {
			return done;
		}

		private void sendToTeamspeak(String message) {
			QuerySender qs = new QuerySender(clid, JTS3ServerQuery.TEXTMESSAGE_TARGET_CLIENT, message);
			BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), qs);
		}

		private void setDone() {
			done = true;
		}
	}
}
