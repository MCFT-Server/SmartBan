package smartban.manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

import java.util.Map.Entry;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.ConfigSection;
import smartban.database.DataBase;

public class BanManager {
	
	private DataBase db;
	private static BanManager instance;
	
	public BanManager(DataBase db) {
		this.db = db;
		instance = this;
	}
	
	public static BanManager getInstance() {
		return instance;
	}
	
	public boolean banPlayer(Player player) {
		return banPlayer(player.getName());
	}
	public boolean banPlayer(String player) {
		return banPlayer(player, db.get("not-write-reason"));
	}
	public boolean banPlayer(Player player, String reason) {
		return banPlayer(player.getName(), reason);
	}
	@SuppressWarnings("serial")
	public boolean banPlayer(String player, String reason) {
		String name = player.toLowerCase();
		if (!hasInfo(name)) {
			return false;
		}
		db.getDB("banlist").set(name, new ConfigSection(new LinkedHashMap<String, Object>() {
			{
				put("ip", getIP(name));
				put("clientid", getClientId(name));
				put("reason", reason);
			}
		}));
		Player playerc = Server.getInstance().getPlayer(player);
		if (playerc != null) {
			playerc.kick(db.get("you-are-banned").replace("%reason", reason), false);
		}
		return true; 
	}
	
	public boolean isBannedName(String player) {
		if (db.getDB("banlist").exists(player.toLowerCase())) return true;
		else return false;
	}
	
	public boolean isBanned(Player player) {
		if (isBanned(player.getAddress()) || isBanned(db.getPlugin().getClientId(player))) {
			return true;
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	public boolean isBanned(String ip) {
		for (Object v1 : db.getDB("banlist").getAll().values()) {
			if (((Map<String, Object>)v1).get("ip").equals(ip)) {
				return true;
			}
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	public boolean isBanned(long clientid) {
		for (Object v1 : db.getDB("banlist").getAll().values()) {
			if (((Map<String, Object>) v1).get("clientid").equals(clientid)) {
				return true;
			}
		}
		return false;
	}
	
	public void pardon(String player) {
		String name = player.toLowerCase();
		db.getDB("banlist").remove(name);
	}
	
	public ArrayList<String> getBanList() {
		ArrayList<String> banlist = new ArrayList<>();
		for (Entry<String, Object> entry : db.getDB("banlist").getAll().entrySet()) {
			banlist.add(entry.getKey());
		}
		return banlist;
	}
	
	@SuppressWarnings("unchecked")
	public String getReason(String player) {
		return (String)((Map<String, Object>)db.getDB("banlist").get(player.toLowerCase())).get("reason");
	}
	@SuppressWarnings("unchecked")
	public String getReason(String ip, long clientid) {
		for (Object v1 : db.getDB("banlist").getAll().values()) {
			Map<String, Object> info = (Map<String, Object>) v1;
			if (info.get("ip").equals(ip) || info.get("clientid").equals(clientid)) {
				return (String) info.get("reason");
			}
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public String getReasonByIP(String ip) {
		for (Object v1 : db.getDB("banlist").getAll().values()) {
			Map<String, Object> info = (Map<String, Object>) v1;
			if (info.get("ip").equals(ip)) {
				return (String) info.get("reason");
			}
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public String getReasonByClientID(long clientid) {
		for (Object v1 : db.getDB("banlist").getAll().values()) {
			Map<String, Object> info = (Map<String, Object>) v1;
			if (info.get("clientid").equals(clientid)) {
				return (String) info.get("reason");
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getInfoByIP(String ip) {
		for (Object v1 : db.getDB("banlist").getAll().values()) {
			Map<String, Object> info = (Map<String, Object>) v1;
			if (info.get("ip").equals(ip)) {
				return (Map<String, Object>) v1;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getInfoByClientId(long clientid) {
		for (Object v1 : db.getDB("banlist").getAll().values()) {
			Map<String, Object> info = (Map<String, Object>) v1;
			if (info.get("clientid").equals(clientid)) {
				return (Map<String, Object>) v1;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private String getIP(String player) {
		try {
			return (String) ((LinkedTreeMap<String, Object>)db.getDB("userinfo").get(player.toLowerCase())).get("ip");
		} catch (ClassCastException e) {
			return db.getDB("userinfo").getSection(player.toLowerCase()).getString("ip");
		}
	}
	@SuppressWarnings("unchecked")
	private long getClientId(String player) {
		try {
			return ((Double) ((LinkedTreeMap<String, Object>)db.getDB("userinfo").get(player.toLowerCase())).get("clientid")).longValue();
		} catch (ClassCastException e) {
			return db.getDB("userinfo").getSection(player.toLowerCase()).getLong("clientid");
		}
	}
	private boolean hasInfo(String player) {
		String name = player.toLowerCase();
		if (db.getDB("userinfo").get(name) == null) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public String findBan(String player) {
		if (!hasInfo(player)) {
			return null;
		}
		for (Entry<String, Object> entry : db.getDB("banlist").getAll().entrySet()) {
			Map<String, Object> map = (Map<String, Object>) entry.getValue();
			if (getIP(player).equals(map.get("ip")) || getClientId(player) == (double) map.get("clientid")) {
				return entry.getKey();
			}
		}
		return null;
	}
}
