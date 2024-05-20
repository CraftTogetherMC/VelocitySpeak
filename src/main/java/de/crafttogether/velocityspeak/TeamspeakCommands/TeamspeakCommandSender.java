package de.crafttogether.velocityspeak.TeamspeakCommands;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import de.crafttogether.velocityspeak.AsyncQueryUtils.QuerySender;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.util.Replacer;
import de.crafttogether.velocityspeak.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TeamspeakCommandSender implements CommandSource {

	private final Map<String, Boolean> permissions;
	private final Map<String, String> client;
	private final String name;
	private final List<String> outBuffer;

	private BufferSender outSender;

	public TeamspeakCommandSender(Map<String, String> clientInfo, Map<String, Boolean> perms) {
		client = clientInfo;
		name = replaceValues(Messages.TS_COMMAND_SENDER_NAME.get());
		outBuffer = Collections.synchronizedList(new LinkedList<String>());

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
	public Tristate getPermissionValue(String perm) {
		return Tristate.fromBoolean(permissions.containsKey(perm) && permissions.get(perm));
	}

	public void setPermission(String s, boolean b) {
		permissions.put(s, b);
	}

	public Collection<String> getPermissions() {
		return null;
	}

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
	public void sendMessage(Component component) {
		if (component.children().isEmpty()) return;
		if (!VelocitySpeak.getQuery().isConnected()) return;

		outBuffer.add(format(LegacyComponentSerializer.legacyAmpersand().serialize(component)));
		startBuffer();
	}

	public void sendMessage(Component... components) {
		if (components == null || components.length == 0) return;

		for (Component component : components) {
			outBuffer.add(format(LegacyComponentSerializer.legacyAmpersand().serialize(component)));
		}

		startBuffer();
	}

	public void sendMessage(String string)  {
		sendMessage(Component.text(string));
	}

	public void sendMessage(String... strings)  {
		for (String string : strings)
			sendMessage(Component.text(string));
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
			QuerySender qs = new QuerySender(clid, TextMessageTargetMode.CLIENT, message);
			VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), qs).schedule();
		}

		private void setDone() {
			done = true;
		}
	}
}
