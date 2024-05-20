package de.crafttogether.velocityspeak;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;

public class ChannelList {

	private ConcurrentHashMap<Integer, ChannelBase> channels;
	private Logger logger;

	public ChannelList() {
		logger = VelocitySpeak.getInstance().getLogger();
		channels = new ConcurrentHashMap<>();
	}

	public void asyncUpdateAll() {
		(new Thread(new ChannelUpdater(this))).start();
	}

	public void asyncUpdateChannel(int cid) {
		if (!channels.containsKey(cid)) return;
		(new Thread(new ChannelUpdater(this, cid))).start();
	}

	public void clear() {
		channels.clear();
	}

	public boolean containsID(int cid) {
		return channels.containsKey(cid);
	}

	public ChannelBase get(int cid) {
		return channels.get(cid);
	}

	public ChannelBase getByName(String name) {
		for (ChannelBase channel : channels.values()) {
			if (channel.get("channel_name").equals(name)) return channel;
		}
		return null;
	}

	public ChannelBase getByPartialName(String name) {

		ChannelBase ret = null;

		for (ChannelBase channel : channels.values()) {
			String n = channel.getName().toLowerCase().replaceAll(" ", "");
			if (n.startsWith(name.toLowerCase())) {
				if (ret == null) {
					ret = channel;
				} else {
					throw new IllegalArgumentException("There is more than one client matching " + name);
				}
			}
		}

		return ret;
	}

	public List<String> getChannelNames() {
		List<String> ret = new ArrayList<String>();
		for (ChannelBase channel : channels.values()) {
			ret.add(channel.getName());
		}
		return ret;
	}

	public ConcurrentHashMap<Integer, ChannelBase> getChannels() {
		return channels;
	}

	public boolean isEmpty() {
		return channels.isEmpty();
	}

	public void removeChannel(int cid) {
		channels.remove(cid);
	}

	public int size() {
		return channels.size();
	}

	public void updateChannel(int cid) {
		(new ChannelUpdater(this, cid)).run();
	}

	public void updateAll() {
		(new ChannelUpdater(this)).run();
	}

	private void setChannelData(ChannelBase channel) {
		if (channel != null && channel.getId() != -1) {
			if (channel.getMap().size() > 1) {
				channels.put(channel.getId(), channel);
			} else {
				channels.remove(channel.getId());
				logger.warning("Received no information for channel id " + channel.getId() + ".");
			}
		}
	}

	private class ChannelUpdater implements Runnable {

		private ChannelList cl;
		private int cid;
		private boolean updateAll;

		public ChannelUpdater(ChannelList channelList, int channelID) {
			cl = channelList;
			cid = channelID;
			updateAll = false;
		}

		public ChannelUpdater(ChannelList channelList) {
			cl = channelList;
			updateAll = true;
		}

		@Override
		public void run() {
			if (!VelocitySpeak.getQuery().isConnected()) return;

			if (updateAll) {
				List<Channel> channelList = VelocitySpeak.getQuery().getApi().getChannels();
				if (channelList == null) {
					VelocitySpeak.log().severe("Error while receiving channel information.");
					return;
				}
				for (Channel channel : channelList) {
					if (channel == null) {
						VelocitySpeak.log().severe("Error while receiving channel information.");
						return;
					}
					cl.setChannelData(channel);
				}
			} else {
				ChannelInfo channel = VelocitySpeak.getQuery().getApi().getChannelInfo(cid);
				if (channel == null) {
					VelocitySpeak.log().severe("Error while receiving channel information.");
					return;
				}
				cl.setChannelData(channel);
			}
		}
	}
}
