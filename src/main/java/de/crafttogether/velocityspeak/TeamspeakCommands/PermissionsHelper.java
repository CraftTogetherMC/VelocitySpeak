package de.crafttogether.velocityspeak.TeamspeakCommands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.github.theholywaffle.teamspeak3.api.PermissionGroupDatabaseType;
import de.crafttogether.velocityspeak.Configuration.YamlConfig;
import de.crafttogether.velocityspeak.VelocitySpeak;

import com.google.common.collect.Lists;

import net.md_5.bungee.config.Configuration;

public final class PermissionsHelper implements Runnable {

	private YamlConfig permissionsConfig;
	private HashMap<Integer, ServerGroup> serverGroupMap;

	public PermissionsHelper() {
		serverGroupMap = new HashMap<>();
	}

	public void runAsynchronously() {
		// Start the permissions assignment task
		VelocitySpeak.getInstance().getProxy().getScheduler().buildTask(VelocitySpeak.getInstance(), this).schedule();
	}

	public void run() {
		// Load the config
		try {
			reload();
		} catch (IOException e) {
			VelocitySpeak.log().severe("Error while loading permissions.yml");
			e.printStackTrace();
		}

		HashMap<Integer, ServerGroup> serverGroups = new HashMap<>();
		HashMap<Integer, HashMap<String, Boolean>> perms = new HashMap<>();
		HashMap<Integer, List<String>> inherits = new HashMap<>();
		Set<String> removedServerGroups = new HashSet<>();
		Queue<Integer> resolved = new LinkedList<>();
		Queue<Integer> unresolved = new LinkedList<>();

		for (String key : permissionsConfig.getKeys()) {
			if (permissionsConfig.isConfigurationSection(key)) {
				removedServerGroups.add(key);
			}
		}

		// Set up a raw list of permissions
		List<com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup> groups = VelocitySpeak.getQuery().getApi().getServerGroups();
		if (groups == null) {
			VelocitySpeak.log().severe("Unable to retrieve Teamspeak ServerGroups.");
			VelocitySpeak.log().severe("This could be caused by a permissions issue.");
			return;
		}
		for (com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup group : groups) {
			int id = group.getId();
			PermissionGroupDatabaseType type = group.getType();
			String name = group.get("name");
			if (name == null || name.isEmpty()) continue;
			if (type != PermissionGroupDatabaseType.REGULAR) continue;

			if (permissionsConfig.isConfigurationSection(String.valueOf(id))) {
				Configuration section = permissionsConfig.getSection(String.valueOf(id));
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
					VelocitySpeak.log().severe("Error parsing TS3 server group " + id + ".");
					continue;
				}

				// Don't waste time if someone is blocked anyways
				if (blocked) {
					serverGroups.put(id, new ServerGroup(name, true));
					perms.put(id, new HashMap<>());
				} else {
					serverGroups.put(id, new ServerGroup(name, op, commandWhitelist, commandBlacklist));
					perms.put(id, parseConfigSection(cs));
				}

				removedServerGroups.remove(String.valueOf(id));
			} else {
				permissionsConfig.set(id + ".name", group.getName());
				permissionsConfig.set(id + ".blocked", false);
				permissionsConfig.set(id + ".op", false);
				permissionsConfig.set(id + ".permissions.somePlugin/permission", true);
				permissionsConfig.set(id + ".permissions.use/slashes/instead/of/dots", true);
				permissionsConfig.set(id + ".plugin-whitelist", Lists.newArrayList("PluginNameFromPluginsCommand"));
				permissionsConfig.set(id + ".command-blacklist", Lists.newArrayList("SomeBlockedCommand"));
				permissionsConfig.set(id + ".inherits", new ArrayList<String>());

				serverGroups.put(id, new ServerGroup(group.getName()));
				perms.put(id, parseConfigSection(permissionsConfig.getSection(id + ".permissions")));
			}
		}

		for (String id : removedServerGroups) {
			permissionsConfig.set(id + ".removed", "This server group has been removed on Teamspeak.");
			VelocitySpeak.log().warning("Obsolete permissions.yml server group entry: ID " + id + ".");
		}

		// Save the config
		save();

		// Get the initial resolved groups
		for (Integer id : serverGroups.keySet()) {
			List<String> i = inherits.get(id);
			if ((i == null) || (i.size() == 0)) {
				resolved.add(id);
				inherits.remove(id);
			} else {
				unresolved.add(id);
			}
		}

		if (resolved.size() == 0) {
			VelocitySpeak.log().severe("Teamspeak permissions: Circular inheritance (No groups with no 'inherits').");
		}

		do {
			// Add the permissions of resolved groups thus resolving the permissions of other groups.

			while (resolved.size() > 0) {
				Integer id = resolved.poll();
				ServerGroup sg = serverGroups.get(id);
				sg.getPermissions().putAll(perms.get(id));

				for (Integer u : inherits.keySet()) {
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
			Integer id = unresolved.poll();
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
			VelocitySpeak.log().warning(sb.toString());

			inherits.put(id, new ArrayList<>()); // Clear the stored dependencies for this one.

			for (Integer u : inherits.keySet()) {
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
				VelocitySpeak.log().warning("Key " + key + " in the permissions for server group "
						+ cKey.split("/")[0] + " did not have a boolean value assigned.");
			}
		}
		return map;
	}

	public ServerGroup getServerGroup(String groupsString) {
		List<Integer> groups = new ArrayList<>();
		for (String gId : groupsString.split(",")) {
			try {
				groups.add(Integer.valueOf(gId));
			} catch (IllegalArgumentException e) {
				VelocitySpeak.log().warning(gId + " is not a valid integer groupd id?");
			}
		}
		return getServerGroup(groups.stream().mapToInt(Integer::intValue).toArray());
	}

	public ServerGroup getServerGroup(int[] groups) {
		if ((groups == null) || (groups.length == 0)) return null;
		if (groups.length == 1) {
			return getSingleServerGroup(groups[0]);
		}

		ServerGroup combined = new ServerGroup("");
		StringBuilder groupName = new StringBuilder("[");
		for (int group : groups) {
			ServerGroup sg = getSingleServerGroup(group);
			if (sg == null) {
				VelocitySpeak.log().warning("Could not resolve server group " + group);
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

	public ServerGroup getSingleServerGroup(int id) {
		return serverGroupMap.get(id);
	}

	public void reload() throws IOException {
		VelocitySpeak.log().info("Loading permissions!");
		permissionsConfig = new YamlConfig(VelocitySpeak.getInstance(), VelocitySpeak.getInstance().getDataDirectory() + File.separator + "permissions.yml");
	}

	public void save() {
		if (permissionsConfig == null) return;
		permissionsConfig.save();
	}
}
