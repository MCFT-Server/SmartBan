package smartban;

import java.lang.reflect.Field;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import smartban.database.DataBase;
import smartban.listener.EventListener;
import smartban.manager.BanManager;
import smartban.manager.WarnManager;

public class Main extends PluginBase {
	private EventListener listener;
	private DataBase db;
	private BanManager banmanager;
	private WarnManager warnmanager;
	
	@Override
	public void onEnable() {
		db = new DataBase(this);
		listener = new EventListener(this);
		banmanager = new BanManager(db);
		warnmanager = new WarnManager(db);
		
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	@Override
	public void onDisable() {
		db.save();
	}
	
	public DataBase getDB() {
		return db;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return listener.onCommand(sender, command, label, args);
	}
	
	public long getClientId(Player player) {
		Class<? extends Player> reflect =  player.getClass();
		Field var;
		long clientId = 0;
		try {
			var = reflect.getDeclaredField("randomClientId");
			var.setAccessible(true);
			try {
				clientId = var.getLong(player);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return clientId;
	}
}
