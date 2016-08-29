package de.redstoneworld.bungeespeak.TeamspeakCommands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import de.redstoneworld.bungeespeak.BungeeSpeak;

import com.google.common.collect.Lists;

import de.redstoneworld.bungeespeak.Configuration.YamlConfig;
import de.stefan1200.jts3serverquery.JTS3ServerQuery;
import net.md_5.bungee.config.Configuration;

public final class PermissionsHelper implements Runnable {

	private YamlConfig permissionsConfig;
	private HashMap<String, ServerGroup> serverGroupMap;

	public PermissionsHelper() {
		serverGroupMap = new HashMap<String, ServerGroup>();
	}

	public void runAsynchronously() {
		// Start the permissions assignment task
		BungeeSpeak.getInstance().getProxy().getScheduler().runAsync(BungeeSpeak.getInstance(), this);
	}

	public void run() {
		// Load the config
		try {
			reload();
		} catch (IOException e) {
			BungeeSpeak.log().severe("Error while loading permissions.yml");
			e.printStackTrace();
		}

		HashMap<String, ServerGroup> serverGroups = new HashMap<String, ServerGroup>();
		HashMap<String, HashMap<String, Boolean>> perms = new HashMap<String, HashMap<String, Boolean>>();
		HashMap<String, List<String>> inherits = new HashMap<String, List<String>>();
		Set<String> removedServerGroups = new HashSet<String>();
		Queue<String> resolved = new LinkedList<String>();
		Queue<String> unresolved = new LinkedList<String>();

		for (String key : permissionsConfig.getKeys()) {
			if (permissionsConfig.isConfigurationSection(key)) {
				removedServerGroups.add(key);
			}
		}

		// Set up a raw list of permissions
		Vector<HashMap<String, String>> groups = BungeeSpeak.getQuery().getList(JTS3ServerQuery.LISTMODE_SERVERGROUPLIST);
		if (groups == null) {
			BungeeSpeak.log().severe("Unable to retrieve Teamspeak ServerGroups.");
			BungeeSpeak.log().severe("This could be caused by a permissions issue.");
			return;
		}
		for (HashMap<String, String> group : groups) {
			String id = group.get("sgid");
			String type = group.get("type");
			String name = group.get("name");
			if (name == null || name.isEmpty()) continue;
			if (type == null || !("1".equals(type))) continue;

			if (permissionsConfig.isConfigurationSection(id)) {
				Configuration section = permissionsConfig.getSection(id);
				section.set("name", group.get("name"));
				if (!(section.get("blocked") instanceof Boolean)) {
					section.set("blocked", false);
				}
				if (!(section.get("op") instanceof Boolean)) {
					section.set("op", false);
				}
				if (!permissionsConfig.isConfigurationSection(id + ".permissions")) {
					section.set(id + ".permissions.somePermission", true);
				}
				if (!(section.get("command-whitelist") instanceof List<?>)) {
					section.set("command-whitelist", Lists.newArrayList("SomeAllowedCommand"));
				}
				if (!(section.get("command-blacklist") instanceof List<?>)) {
					section.set("command-blacklist", Lists.newArrayList("SomeBlockedCommand"));
				}
				boolean op = section.getBoolean("op");
				boolean blocked = section.getBoolean("blocked");
				Configuration cs = section.getSection("permissions");
				List<String> commandWhitelist = section.getStringList("command-whitelist");
				List<String> commandBlacklist = section.getStringList("command-blacklist");
				inherits.put(id, section.getStringList("inherits"));

				if (cs.getKeys().isEmpty() || commandWhitelist == null || commandBlacklist == null) {
					BungeeSpeak.log().severe("Error parsing TS3 server group " + id + ".");
					continue;
				}

				// Don't waste time if someone is blocked anyways
				if (blocked) {
					serverGroups.put(id, new ServerGroup(name, true));
					perms.put(id, new HashMap<String, Boolean>());
				} else {
					serverGroups.put(id, new ServerGroup(name, op, commandWhitelist, commandBlacklist));
					perms.put(id, parseConfigSection(cs));
				}

				removedServerGroups.remove(id);
			} else {
				permissionsConfig.set(id + ".name", group.get("name"));
				permissionsConfig.set(id + ".blocked", false);
				permissionsConfig.set(id + ".op", false);
				permissionsConfig.set(id + ".permissions.somePlugin/permission", true);
				permissionsConfig.set(id + ".permissions.OR_plugin/permission", true);
				permissionsConfig.set(id + ".plugin-whitelist", Lists.newArrayList("PluginNameFromPluginsCommand"));
				permissionsConfig.set(id + ".command-blacklist", Lists.newArrayList("SomeBlockedCommand"));
				permissionsConfig.set(id + ".inherits", new ArrayList<String>());

				serverGroups.put(id, new ServerGroup(group.get("name")));
				perms.put(id, parseConfigSection(permissionsConfig.getSection(id + ".permissions")));
			}
		}

		for (String id : removedServerGroups) {
			permissionsConfig.set(id + ".removed", "This server group has been removed on Teamspeak.");
			BungeeSpeak.log().warning("Obsolete permissions.yml server group entry: ID " + id + ".");
		}

		// Save the config
		save();

		// Get the initial resolved groups
		for (String id : serverGroups.keySet()) {
			List<String> i = inherits.get(id);
			if ((i == null) || (i.size() == 0)) {
				resolved.add(id);
				inherits.remove(id);
			} else {
				unresolved.add(id);
			}
		}

		if (resolved.size() == 0) {
			BungeeSpeak.log().severe("Teamspeak permissions: Circular inheritance (No groups with no 'inherits').");
		}

		do {
			// Add the permissions of resolved groups thus resolving the permissions of other groups.

			while (resolved.size() > 0) {
				String id = resolved.poll();
				ServerGroup sg = serverGroups.get(id);
				sg.getPermissions().putAll(perms.get(id));

				for (String u : inherits.keySet()) {
					List<String> i = inherits.get(u);
					if ((i.size() > 0) && (i.contains(id))) {
						i.remove(id);
						ServerGroup target = serverGroups.get(u);
						target.getPermissions().putAll(sg.getPermissions());
						target.getCommandWhitelist().addAll(sg.getCommandWhitelist());
						target.getCommandBlacklist().addAll(sg.getCommandBlacklist());
						if (i.size() == 0) {
							resolved.add(u);
							unresolved.remove(u);
						}
					}
				}
			}

			// When everything went well, this queue is now empty.
			if (unresolved.size() == 0) {
				break;
			}

			// Otherwise, just choose a group with an unresolved dependency
			String id = unresolved.poll();
			ServerGroup sg = serverGroups.get(id);
			sg.getPermissions().putAll(perms.get(id));

			// Notify about unresolved dependencies.
			List<String> inheritances = inherits.get(id);
			StringBuilder sb = new StringBuilder();
			sb.append("Server group ").append(id).append(" had unresolved dependencies: ");
			for (String i : inheritances) {
				sb.append(i).append(", ");
			}
			sb.setLength(sb.length() - 2);
			sb.append(".");
			BungeeSpeak.log().warning(sb.toString());

			inherits.put(id, new ArrayList<String>()); // Clear the stored dependencies for this one.

			for (String u : inherits.keySet()) {
				List<String> i = inherits.get(u);
				if ((i.size() > 0) && (i.contains(id))) {
					i.remove(id);
					serverGroups.get(u).getPermissions().putAll(sg.getPermissions());
					if (i.size() == 0) {
						resolved.add(u);
						unresolved.remove(u);
					}
				}
			}
		} while (unresolved.size() > 0);

		// Done
		serverGroupMap = serverGroups;
	}

	private HashMap<String, Boolean> parseConfigSection(Configuration cs) {
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		for (String cKey : cs.getKeys()) {
			Object value = cs.get(cKey);
			String key = cKey.replace('/', '.');
			if (value instanceof Boolean) {
				map.put(key, (Boolean) value);
			} else if (!(value instanceof Configuration)) {
				BungeeSpeak.log().warning("Key " + key + " in the permissions for server group "
						+ cKey.split("/")[0] + " did not have a boolean value assigned.");
			}
		}
		return map;
	}

	public ServerGroup getServerGroup(String entry) {
		if ((entry == null) || (entry.isEmpty())) return null;
		if (!entry.contains(",")) {
			return getSingleServerGroup(entry);
		}

		ServerGroup combined = new ServerGroup("");
		StringBuilder groupName = new StringBuilder("[");
		for (String group : entry.split(",")) {
			ServerGroup sg = getSingleServerGroup(group);
			if (sg == null) {
				BungeeSpeak.log().warning("Could not resolve server group " + group);
				continue;
			}

			// If one group is blocked, the resulting group should be blocked, too
			if (sg.isBlocked()) return new ServerGroup(sg.getName(), true);

			groupName.append(sg.getName()).append(", ");
			combined.setOp(combined.isOp() || sg.isOp());
			combined.getPermissions().putAll(sg.getPermissions());
			combined.getCommandWhitelist().addAll(sg.getCommandWhitelist());
			combined.getCommandBlacklist().addAll(sg.getCommandBlacklist());
		}

		groupName.setLength(groupName.length() - 2);
		combined.setName(groupName.append("]").toString());
		return combined;
	}

	public ServerGroup getSingleServerGroup(String id) {
		return serverGroupMap.get(id);
	}

	public void reload() throws IOException {
		BungeeSpeak.log().info("Loading permissions!");
		permissionsConfig = new YamlConfig(BungeeSpeak.getInstance(), BungeeSpeak.getInstance().getDataFolder() + File.separator + "permissions.yml");
	}

	public void save() {
		if (permissionsConfig == null) return;
		permissionsConfig.save();
	}
}
