package de.crafttogether.velocityspeak.Commands;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.velocitypowered.api.command.CommandSource;
import de.crafttogether.velocityspeak.VelocitySpeak;
import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.Configuration.Messages;
import de.crafttogether.velocityspeak.TeamspeakCommands.PermissionsHelper;
import de.crafttogether.velocityspeak.TeamspeakCommands.ServerGroup;
import de.crafttogether.velocityspeak.util.MessageUtil;
import de.crafttogether.velocityspeak.util.Replacer;

public class CommandList extends BungeeSpeakCommand {

	private static final String[] VALUES = {"server", "channel"};

	public CommandList() {
		super("list");
	}

	@Override
	public void execute(CommandSource source, String[] args) {
		if (!isConnected(source)) return;

		if (args.length < 2 || args[1].equalsIgnoreCase("server")) {
			Collection<Client> clientCollection;
			if (Configuration.MC_COMMANDS_CLIENTLIST_FILTER_LIST.getBoolean()) {
                clientCollection = VelocitySpeak.getClientList().getFilteredClients().values();
            } else {
                clientCollection = VelocitySpeak.getClientList().getClients().values();
            }
			List<Client> clients = new ArrayList<>(clientCollection);
			Iterator<Client> iterator = clients.iterator();
			while (iterator.hasNext()) {
				Client user = iterator.next();
				if (user.getType() != 0) {
					iterator.remove();
				}
			}

			String mcMsg = Messages.MC_COMMAND_LIST_SERVER.get();
			String mainColor = MessageUtil.getFormatString(mcMsg);
			String secondaryColor = MessageUtil.getSecondaryFormatString(mcMsg);
			String userList = createClientList(clients, Configuration.TS_LIST_GROUPING.getBoolean(), mainColor, secondaryColor);
			mcMsg = new Replacer().addSender(source).addList(userList).addCount(clients.size()).replace(mcMsg);

			if (mcMsg == null || mcMsg.isEmpty()) return;
			send(source, Level.INFO, mcMsg);
		} else if (args.length == 2 && Configuration.TS_ENABLE_CHANNEL_EVENTS.getBoolean()
				&& args[1].equalsIgnoreCase("channel")) {
			int id = VelocitySpeak.getQueryInfo().getChannelId();

			Collection<Client> clientCollection;
			if (Configuration.MC_COMMANDS_CLIENTLIST_FILTER_LIST.getBoolean()) {
				clientCollection = VelocitySpeak.getClientList().getFilteredClients().values();
			} else {
				clientCollection = VelocitySpeak.getClientList().getClients().values();
			}
			List<Client> clients = new ArrayList<>(clientCollection);
			clients.removeIf(user -> user.getType() != 0 || user.getChannelId() != id);

			String mcMsg = Messages.MC_COMMAND_LIST_CHANNEL.get();
			String mainColor = MessageUtil.getFormatString(mcMsg);
			String secondaryColor = MessageUtil.getSecondaryFormatString(mcMsg);
			String userList = createClientList(clients, Configuration.TS_LIST_GROUPING.getBoolean(), mainColor, secondaryColor);
			mcMsg = new Replacer().addSender(source).addList(userList).addCount(clients.size()).replace(mcMsg);

			if (mcMsg == null || mcMsg.isEmpty()) return;
			send(source, Level.INFO, mcMsg);
		} else {
			Replacer r = new Replacer().addSender(source).addCommandUsage("/ts list (server / channel)");
			String usageMessage = r.replace(Messages.MC_COMMAND_ERROR_MESSAGE_USAGE.get());
			send(source, Level.WARNING, usageMessage);
		}
	}

	private static String createClientList(List<Client> clients, boolean grouping, String mainColor, String secondaryColor) {
		if (clients.isEmpty()) return "-";

		if (grouping) {
			PermissionsHelper permHelper = VelocitySpeak.getPermissionsHelper();
			Map<String, List<Client>> groups = new HashMap<>();

			// Group
			for (Client user : clients) {
				ServerGroup sg = permHelper.getServerGroup(user.getServerGroups());
				if (sg == null) {
					VelocitySpeak.log().warning("Could not resolve server group(s) for user \""
							+ user.get("client_nickname") + "\".");
					VelocitySpeak.log().warning("Server groups: " + user.get("client_servergroups"));
					continue;
				}

				if (groups.containsKey(sg.getName())) {
					groups.get(sg.getName()).add(user);
				} else {
					List<Client> newGroup = new ArrayList<>();
					newGroup.add(user);
					groups.put(sg.getName(), newGroup);
				}
			}
			if (groups.isEmpty()) return "-";

			// Stringify
			StringBuilder output = new StringBuilder("\n");
			for (Entry<String, List<Client>> entry : groups.entrySet()) {
				output.append("&r").append(secondaryColor).append(entry.getKey()).append(": ");
				for (Client user : entry.getValue()) {
					output.append(mainColor).append(user.getNickname());
					output.append("&r").append(secondaryColor).append(", ");
				}
				output.setLength(output.length() - 6);
				output.append("\n");
			}
			output.setLength(output.length() - 1);
			return output.toString();
		} else {
			StringBuilder list = new StringBuilder();
			for (Client user : clients) {
				if (user.getType() == 0) {
					list.append(mainColor).append(user.getNickname());
					list.append("&r").append(secondaryColor).append(", ");
				}
			}

			list.setLength(list.length() - 6);
			return list.toString();
		}
	}

	@Override
	public List<String> onTabComplete(CommandSource source, String[] args) {
		if (args.length != 2) return Collections.emptyList();
		List<String> al = new ArrayList<String>();
		for (String n : VALUES) {
			if (n.startsWith(args[1])) al.add(n);
		}
		return al;
	}
}
