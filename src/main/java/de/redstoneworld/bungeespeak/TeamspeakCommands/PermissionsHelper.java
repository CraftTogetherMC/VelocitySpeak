package de.redstoneworld.bungeespeak.TeamspeakCommands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import de.redstoneworld.bungeespeak.BungeeSpeak;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Lists;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public final class PermissionsHelper implements Runnable {

	private File permissionsFile;
	private FileConfiguration permissionsConfig;
	private HashMap<String, ServerGroup> serverGroupMap;

	public PermissionsHelper() {
		serverGroupMap = new HashMap<String, ServerGroup>();
	}

	public void runAsynchronously() {
		// Start the permissions assignment task
		BungeeSpeak.getInstance().getServer().getScheduler().runTaskAsynchronously(BungeeSpeak.getInstance(), this);
	}

	public void run() {
		// Load the config
		reload();

		HashMap<String, ServerGroup> serverGroups = new HashMap<String, ServerGroup>();
		HashMap<String, HashMap<String, Boolean>> perms = new HashMap<String, HashMap<String, Boolean>>();
		HashMap<String, List<String>> inherits = new HashMap<String, List<String>>();
		Set<String> removedServerGroups = new HashSet<String>();
		Queue<String> resolved = new LinkedList<String>();
		Queue<String> unresolved = new LinkedList<String>();

		for (String key : permissionsConfig.getKeys(false)) {
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
				ConfigurationSection section = permissionsConfig.getConfigurationSection(id);
				section.set("name", group.get("name"));
				if (!section.isBoolean("blocked")) {
					section.set("blocked", false);
				}
				if (!section.isBoolean("op")) {
					section.set("op", false);
				}
				if (!section.isConfigurationSection("permissions")) {
					section.createSection("permissions").set("somePermission", true);
				}
				if (!section.isList("plugin-whitelist")) {
					section.set("plugin-whitelist", (List<String>) Lists.newArrayList("PluginNameFromPluginsCommand"));
				}
				if (!section.isList("command-blacklist")) {
					section.set("command-blacklist", (List<String>) Lists.newArrayList("SomeBlockedCommand"));
				}
				Boolean op = section.getBoolean("op");
				Boolean blocked = section.getBoolean("blocked");
				ConfigurationSection cs = section.getConfigurationSection("permissions");
				List<String> pluginWhitelist = section.getStringList("plugin-whitelist");
				List<String> commandBlacklist = section.getStringList("command-blacklist");
				inherits.put(id, section.getStringList("inherits"));

				if (op == null || blocked == null || cs == null || pluginWhitelist == null || commandBlacklist == null
						|| inherits == null) {
					BungeeSpeak.log().severe("Error parsing TS3 server group " + id + ".");
					continue;
				}

				// Don't waste time if someone is blocked anyways
				if (blocked.booleanValue()) {
					serverGroups.put(id, new ServerGroup(name, true));
					perms.put(id, new HashMap<String, Boolean>());
				} else {
					serverGroups.put(id, new ServerGroup(name, op.booleanValue(), pluginWhitelist, commandBlacklist));
					perms.put(id, parseConfigSection(cs));
				}

				removedServerGroups.remove(id);
			} else {
				ConfigurationSection section = permissionsConfig.createSection(id);
				section.set("name", group.get("name"));
				section.set("blocked", false);
				section.set("op", false);
				section.createSection("permissions").set("somePlugin.permission", true);
				section.getConfigurationSection("permissions").createSection("OR_plugin").set("permission", true);
				section.set("plugin-whitelist", (List<String>) Lists.newArrayList("PluginNameFromPluginsCommand"));
				section.set("command-blacklist", (List<String>) Lists.newArrayList("SomeBlockedCommand"));
				section.set("inherits", (List<String>) new ArrayList<String>());

				serverGroups.put(id, new ServerGroup(group.get("name")));
				perms.put(id, parseConfigSection(section.getConfigurationSection("permissions")));
			}
		}

		for (String id : removedServerGroups) {
			permissionsConfig.getConfigurationSection(id).set("removed", "This server group has been removed on Teamspeak.");
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
						target.getPluginWhitelist().addAll(sg.getPluginWhitelist());
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

	private HashMap<String, Boolean> parseConfigSection(ConfigurationSection cs) {
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		for (Map.Entry<String, Object> entry : cs.getValues(true).entrySet()) {
			String key = entry.getKey().replace('/', '.');
			if (entry.getValue() instanceof Boolean) {
				map.put(key, (Boolean) entry.getValue());
			} else if (!(entry.getValue() instanceof ConfigurationSection)) {
				BungeeSpeak.log().warning("Key " + key + " in the permissions for server group "
						+ cs.getCurrentPath().split("/")[0] + " did not have a boolean value assigned.");
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
			combined.getPluginWhitelist().addAll(sg.getPluginWhitelist());
			combined.getCommandBlacklist().addAll(sg.getCommandBlacklist());
		}

		groupName.setLength(groupName.length() - 2);
		combined.setName(groupName.append("]").toString());
		return combined;
	}

	public ServerGroup getSingleServerGroup(String id) {
		return serverGroupMap.get(id);
	}

	public void reload() {
		if (permissionsFile == null) {
			permissionsFile = new File(BungeeSpeak.getInstance().getDataFolder(), "permissions.yml");
		}
		permissionsConfig = YamlConfiguration.loadConfiguration(permissionsFile);
		permissionsConfig.options().pathSeparator('/');
	}

	public void save() {
		if ((permissionsFile == null) || (permissionsConfig == null)) return;
		try {
			permissionsConfig.save(permissionsFile);
		} catch (IOException e) {
			BungeeSpeak.log().log(Level.SEVERE, "Could not save the locale file to " + permissionsFile, e);
		}
	}
}
