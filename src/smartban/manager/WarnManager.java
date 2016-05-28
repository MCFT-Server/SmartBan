package smartban.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import smartban.database.DataBase;

public class WarnManager {
	
	private DataBase db;
	private static WarnManager instance;
	
	public WarnManager(DataBase db) {
		this.db = db;
		instance = this;
	}
	public static WarnManager getInstance() {
		return instance;
	}

	public void addWarn(Player player) {
		addWarn(player.getName(), 1);
	}
	public void addWarn(String player) {
		addWarn(player, 1);
	}
	public void addWarn(Player player, int count) {
		addWarn(player.getName(), count);
	}
	public void addWarn(String player, int count) {
		String name = player.toLowerCase();
		db.getDB("warndb").set(name, getWarn(name) + count);
		Player playerc = Server.getInstance().getPlayer(name);
		if (playerc != null) {
			db.message(playerc, db.get("you-warn").replace("%count", Integer.toString(count)));
		}
		if (getWarn(name) >= db.getDB("config").getInt("max-warn")) {
			BanManager.getInstance().banPlayer(name, db.get("excess-warn"));
		}
	}
	
	public void reduceWarn(Player player) {
		reduceWarn(player.getName(), 1);
	}
	public void reduceWarn(String player) {
		reduceWarn(player, 1);
	}
	public void reduceWarn(Player player, int count) {
		reduceWarn(player.getName(), count);
	}
	public void reduceWarn(String player, int count) {
		String name = player.toLowerCase();
		db.getDB("warndb").set(name, getWarn(name) - count);
		if (getWarn(player) < 0) {
			db.getDB("warndb").set(name, 0);
		}
	}
	
	public int getWarn(Player player) {
		return getWarn(player.getName());
	}
	public int getWarn(String player) {
		return db.getDB("warndb").getInt(player.toLowerCase());
	}
}
