package smartban.database;

import java.util.LinkedHashMap;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import smartban.Main;

public class DataBase extends BaseDB<Main> {
	public DataBase(Main plugin) {
		super(plugin);
		init();
	}
	@SuppressWarnings("serial")
	private void init() {
		initMessage();
		initDB("banlist", plugin.getDataFolder().getPath() + "/banlist.json", Config.JSON);
		initDB("userinfo", plugin.getDataFolder().getPath() + "/userinfo.json", Config.JSON);
		initDB("warndb", plugin.getDataFolder().getPath() + "/warnDB.json", Config.JSON);
		initDB("config", plugin.getDataFolder().getPath() + "/config.yml", Config.YAML, new ConfigSection(new LinkedHashMap<String, Object>() {
			{
				put("max-warn", 3);
			}
		}));
		registerCommands();
	}
	private void registerCommands() {
		registerCommand(get("commands-ban"), get("commands-ban-description"), get("commands-ban-usage"), "smartban.commands.ban.ban");
		registerCommand(get("commands-warn"), get("commands-warn-description"), get("commands-warn-usage"), "smartban.commadns.warn.warn");
	}
	
	public Main getPlugin() {
		return plugin;
	}
}
