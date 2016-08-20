package de.redstoneworld.bungeespeak.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import de.redstoneworld.bungeespeak.Configuration.Configuration;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Replacer {

	private Map<String, String> repl;

	public Replacer() {
		repl = new HashMap<String, String>();
	}

	public Replacer addKey(String key, String value) {
		repl.put(key, value);
		return this;
	}

	public Replacer addPlayer(ProxiedPlayer player) {
		repl.put("player_name", player.getName());
		repl.put("player_displayname", player.getDisplayName());
		return this;
	}

	public Replacer addClient(Map<String, String> client) {
		repl.putAll(client);
		return this;
	}

	public Replacer addSender(CommandSender sender) {
		repl.put("player_name", getSenderName(sender));
		repl.put("player_displayname", getSenderDisplayName(sender));
		return this;
	}

	public Replacer addTargetClient(Map<String, String> client) {
		repl.put("target", client.get("client_nickname"));
		repl.putAll(client);
		return this;
	}

	public Replacer addChannel(Map<String, String> channel) {
		repl.put("channel", channel.get("channel_name"));
		repl.put("description", channel.get("channel_description"));
		repl.put("topic", channel.get("channel_topic"));
		return this;
	}

	public Replacer addMessage(String message) {
		repl.put("msg", message);
		return this;
	}

	public Replacer addCount(int count) {
		repl.put("count", String.valueOf(count));
		return this;
	}

	public Replacer addList(String list) {
		repl.put("list", list);
		return this;
	}

	public Replacer addInput(String input) {
		repl.put("input", String.valueOf(input));
		return this;
	}

	public Replacer addCommandUsage(String usage) {
		repl.put("usage", usage);
		return this;
	}

	public Replacer addCommandDescription(String command, String description) {
		repl.put("command", command);
		repl.put("description", description);
		return this;
	}

	public String replace(String input) {
		if (input == null) return null;

		String s = input;
		for (String key : repl.keySet()) {
			String v = repl.get(key);
			if (v == null) continue;
			s = s.replaceAll("%" + key + "%", Matcher.quoteReplacement(v));
		}
		return s;
	}

	private String getSenderName(CommandSender sender) {
		if (sender instanceof ProxiedPlayer) {
			return sender.getName();
		} else {
			return MessageUtil.toMinecraft(Configuration.TS_CONSOLE_NAME.getString(), false, false);
		}
	}

	private String getSenderDisplayName(CommandSender sender) {
		if (sender instanceof ProxiedPlayer) {
			return ((ProxiedPlayer) sender).getDisplayName();
		} else {
			return Configuration.TS_CONSOLE_NAME.getString();
		}
	}
}
