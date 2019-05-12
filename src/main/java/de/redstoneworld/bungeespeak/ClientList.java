package de.redstoneworld.bungeespeak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.redstoneworld.bungeespeak.Configuration.Configuration;

public class ClientList {

	private ConcurrentHashMap<Integer, Client> clients;
	private Logger logger;

	public ClientList() {
		logger = BungeeSpeak.getInstance().getLogger();
		clients = new ConcurrentHashMap<>();
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

	public Client get(int clid) {
		return clients.get(clid);
	}

	public Client getByName(String name) {
		for (Client client : clients.values()) {
			if (client.getNickname().equals(name)) return client;
		}
		return null;
	}

	public Client getByPartialName(String name) {

		Client ret = null;

		for (Client client : clients.values()) {
			String n = client.getNickname().toLowerCase().replaceAll(" ", "");
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
		for (Client client : clients.values()) {
			ret.add(client.getNickname());
		}
		return ret;
	}

	public ConcurrentHashMap<Integer, Client> getFilteredClients() {
		if (Configuration.MC_COMMANDS_CLIENTLIST_FILTER_RULES.getStringList().size() == 0) {
			return clients;
		}
		ConcurrentHashMap<Integer, Client> filteredClients = new ConcurrentHashMap<>();
		for (Map.Entry<Integer, Client> client : clients.entrySet()) {
			boolean add = true;
			for (String rule : Configuration.MC_COMMANDS_CLIENTLIST_FILTER_RULES.getStringList()) {
				if (client.getValue().getNickname().matches(rule)) {
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

	public ConcurrentHashMap<Integer, Client> getClients() {
		return clients;
	}

	public boolean isEmpty() {
		return clients.isEmpty();
	}

	public void removeClient(int clid) {
		clients.remove(clid);
	}

	public int size() {
		return clients.size();
	}

	public void updateClient(int clid) {
		// This should prevent a wrong result in #containsID(int) while the data is still being updated
		clients.put(clid, new Client(new HashMap<>()));

		(new ClientUpdater(this, clid)).run();
	}

	public void updateAll() {
		(new ClientUpdater(this)).run();
	}

	private void setClientData(Client client) {
		if (client != null && client.getId() != -1) {
			if (client.getMap().size() > 1) {
				if (client.getType() == 0) {
					clients.put(client.getId(), client);
				}
			} else {
				clients.remove(client.getId());
				logger.warning("Received no information for client id " + client.getId() + ".");
			}
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
				List<Client> clientList = BungeeSpeak.getQuery().getApi().getClients();
				if (clientList == null) {
					BungeeSpeak.log().severe("Error while receiving client information.");
					return;
				}
				for (Client client : clientList) {
					if (client == null) {
						BungeeSpeak.log().severe("Error while receiving client information.");
						return;
					}
					cl.setClientData(client);
				}
			} else {
				ClientInfo client = BungeeSpeak.getQuery().getApi().getClientInfo(clid);
				if (client == null) {
					BungeeSpeak.log().severe("Error while receiving client information.");
					return;
				}
				cl.setClientData(client);
			}
		}
	}
}
