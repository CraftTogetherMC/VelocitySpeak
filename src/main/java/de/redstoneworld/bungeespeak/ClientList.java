package de.redstoneworld.bungeespeak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import de.redstoneworld.bungeespeak.Configuration.Configuration;
import de.stefan1200.jts3serverquery.JTS3ServerQuery;

public class ClientList {

	private ConcurrentHashMap<Integer, HashMap<String, String>> clients;
	private Logger logger;

	public ClientList() {
		logger = BungeeSpeak.getInstance().getLogger();
		clients = new ConcurrentHashMap<Integer, HashMap<String, String>>();
	}

	public void asyncUpdateAll() {
		(new Thread(new ClientUpdater(this))).start();
	}

	public void asyncUpdateClient(int clid) {
		if (!clients.containsKey(clid)) return;
		(new Thread(new ClientUpdater(this, clid))).start();
	}

	public void clear() {
		clients.clear();
	}

	public boolean containsID(int clid) {
		return clients.containsKey(clid);
	}

	public HashMap<String, String> get(int clid) {
		return clients.get(clid);
	}

	public HashMap<String, String> getByName(String name) {
		for (HashMap<String, String> client : clients.values()) {
			if (client.get("client_nickname").equals(name)) return client;
		}
		return null;
	}

	public HashMap<String, String> getByPartialName(String name) {

		HashMap<String, String> ret = null;

		for (HashMap<String, String> client : clients.values()) {
			String n = client.get("client_nickname").toLowerCase().replaceAll(" ", "");
			if (n.startsWith(name.toLowerCase())) {
				if (ret == null) {
					ret = client;
				} else {
					throw new IllegalArgumentException("There is more than one client matching " + name);
				}
			}
		}

		return ret;
	}

	public List<String> getClientNames() {
		List<String> ret = new ArrayList<String>();
		for (HashMap<String, String> client : clients.values()) {
			ret.add(client.get("client_nickname"));
		}
		return ret;
	}

	public ConcurrentHashMap<Integer, HashMap<String, String>> getFilteredClients() {
		if (Configuration.MC_COMMANDS_CLIENTLIST_FILTER_RULES.getStringList().size() == 0) {
			return clients;
		}
		ConcurrentHashMap<Integer, HashMap<String, String>> filteredClients = new ConcurrentHashMap<Integer, HashMap<String, String>>();
		for (Map.Entry<Integer, HashMap<String, String>> client : clients.entrySet()) {
			boolean add = true;
			for (String rule : Configuration.MC_COMMANDS_CLIENTLIST_FILTER_RULES.getStringList()) {
				if (client.getValue().get("client_nickname").matches(rule)) {
					add = false;
					break;
				}
			}
			if (add) {
				filteredClients.put(client.getKey(), client.getValue());
			}
		}
		return filteredClients;
	}

	public ConcurrentHashMap<Integer, HashMap<String, String>> getClients() {
		return clients;
	}

	public boolean isEmpty() {
		return clients.isEmpty();
	}

	public void removeClient(int clid) {
		if (clients.containsKey(clid)) clients.remove(clid);
	}

	public int size() {
		return clients.size();
	}

	public void updateClient(int clid) {
		// This should prevent a wrong result in #containsID(int) while the data is still being updated
		clients.put(clid, new HashMap<String, String>());

		(new ClientUpdater(this, clid)).run();
	}

	public void updateAll() {
		(new ClientUpdater(this)).run();
	}

	private void setClientData(HashMap<String, String> client, int clid) {
		if (client != null && client.size() != 0) {
			if ("0".equals(client.get("client_type"))) {
				client.put("clid", String.valueOf(clid));
				clients.put(clid, client);
			}
		} else {
			clients.remove(clid);
			logger.warning("Received no information for client id " + clid + ".");
		}
	}

	private class ClientUpdater implements Runnable {

		private ClientList cl;
		private int clid;
		private boolean updateAll;

		public ClientUpdater(ClientList clientList, int clientID) {
			cl = clientList;
			clid = clientID;
			updateAll = false;
		}

		public ClientUpdater(ClientList clientList) {
			cl = clientList;
			updateAll = true;
		}

		@Override
		public void run() {
			if (!BungeeSpeak.getQuery().isConnected()) return;

			if (updateAll) {
				Vector<HashMap<String, String>> clientList;
				clientList = BungeeSpeak.getQuery().getList(JTS3ServerQuery.LISTMODE_CLIENTLIST, "-info,-groups,-country");
				if (clientList == null) {
					BungeeSpeak.log().severe("Error while receiving client information.");
					return;
				}
				for (HashMap<String, String> client : clientList) {
					if (client == null) {
						BungeeSpeak.log().severe("Error while receiving client information.");
						return;
					}
					cl.setClientData(client, Integer.valueOf(client.get("clid")));
				}
			} else {
				HashMap<String, String> client = BungeeSpeak.getQuery().getInfo(JTS3ServerQuery.INFOMODE_CLIENTINFO, clid);
				if (client == null) {
					BungeeSpeak.log().severe("Error while receiving client information.");
					return;
				}
				cl.setClientData(client, clid);
			}
		}
	}
}
