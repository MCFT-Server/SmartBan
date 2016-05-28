package smartban.listener;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import smartban.Main;
import smartban.database.DataBase;
import smartban.manager.BanManager;
import smartban.manager.WarnManager;

public class EventListener implements Listener {
	private Main plugin;
	private DataBase db;
	
	public EventListener(Main plugin) {
		this.plugin = plugin;
		this.db = plugin.getDB();
	}
	
	public Main getPlugin() {
		return plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().toLowerCase().equals(db.get("commands-ban"))) {
			if (args.length < 1) {
				db.alert(sender, db.get("commands-ban-usage"));
				return true;
			}
			if (args[0].toLowerCase().equals(db.get("commands-add"))) {
				if (args.length < 2) {
					db.alert(sender, db.get("commands-ban-add-usage"));
					return true;
				}
				boolean successban;
				if (args.length < 3) {
					successban = BanManager.getInstance().banPlayer(args[1]);
				} else {
					successban = BanManager.getInstance().banPlayer(args[1], String.join(" ", args).substring(args[0].length() + args[1].length() + 1));
				}
				if (!successban) {
					db.alert(sender, db.get("cant-find-player"));
					return true;
				}
				db.message(sender, db.get("success-banned"));
				return true;
			} else if (args[0].toLowerCase().equals(db.get("commands-remove"))) {
				if (args.length < 2) {
					db.alert(sender, db.get("commands-ban-remove-usage"));
					return true;
				}
				if (!BanManager.getInstance().isBannedName(args[1])) {
					db.alert(sender, db.get("not-banned"));
					return true;
				}
				BanManager.getInstance().pardon(args[1]);
				db.message(sender, db.get("success-pardon"));
				return true;
			} else if (args[0].toLowerCase().equals(db.get("commands-list"))) {
				db.message(sender, db.get("ban-list"));
				String banlist = "";
				for (String player : BanManager.getInstance().getBanList()) {
					banlist = banlist + player + ", ";
				}
				db.message(sender, banlist);
				return true;
			} else if (args[0].toLowerCase().equals(db.get("commands-reason"))) {
				if (args.length < 2) {
					db.alert(sender, db.get("commands-ban-reason-usage"));
					return true;
				}
				if (!BanManager.getInstance().isBannedName(args[1])) {
					db.alert(sender, db.get("not-banned"));
					return true;
				}
				db.message(sender, db.get("ban-reason").replace("%player", args[1]).replace("%reason", BanManager.getInstance().getReason(args[1])));
				return true;
			} else {
				db.alert(sender, db.get("commands-ban-usage"));
				return true;
			}
		} else if (command.getName().toLowerCase().equals(db.get("commands-warn"))) {
			if (args.length < 1) {
				if (sender.isOp())
					db.alert(sender, db.get("commands-warn-opusage"));
				else {
					db.alert(sender, db.get("commands-warn-usage"));
				}
				return true;
			}
			if (args[0].toLowerCase().equals(db.get("commands-mywarn"))) {
				if (!sender.hasPermission("smartban.commands.warn.warn")) {
					sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
					return true;
				}
				if (! (sender instanceof Player)) {
					sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.ingame"));
					return true;
				}
				db.message(sender, db.get("mywarn").replace("%count", Integer.toString(WarnManager.getInstance().getWarn((Player)sender))));
				return true;
			} else if (args[0].toLowerCase().equals(db.get("commands-see"))) {
				if (!sender.hasPermission("smartban.commands.warn.see")) {
					sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
					return true;
				}
				if (args.length < 2) {
					db.alert(sender, db.get("commands-warn-see-usage"));
					return true;
				}
				db.message(sender, db.get("players-warn").replace("%player", args[1]).replace("%count", Integer.toString(WarnManager.getInstance().getWarn(args[1]))));
				return true;
			} else if (args[0].toLowerCase().equals(db.get("commands-add"))) {
				if (!sender.hasPermission("smartban.commands.warn.add")) {
					sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
					return true;
				}
				if (args.length < 3) {
					WarnManager.getInstance().addWarn(args[1]);
					db.message(sender, db.get("warn-success").replace("%player", args[1]).replace("%count", "1"));
				} else {
					try {
						WarnManager.getInstance().addWarn(args[1], Integer.parseInt(args[2]));
						db.message(sender, db.get("warn-success").replace("%player", args[1]).replace("%count", args[2]));
					} catch (NumberFormatException e) {
						db.alert(sender, db.get("count-must-be-integer"));
						return true;
					}
				}
				return true;
			} else if (args[0].toLowerCase().equals(db.get("commands-reduce"))) {
				if (!sender.hasPermission("smartban.commands.warn.reduce")) {
					sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
					return true;
				}
				if (args.length < 3) {
					WarnManager.getInstance().reduceWarn(args[1]);
					db.message(sender, db.get("reduce-warn-success").replace("%player", args[2]).replace("%count", "1"));
				} else {
					try {
						WarnManager.getInstance().reduceWarn(args[1], Integer.parseInt(args[2]));
						db.message(sender, db.get("reduce-warn-success").replace("%player", args[1]).replace("%count", args[2]));
					} catch (NumberFormatException e) {
						db.alert(sender, db.get("count-must-be-integer"));
						return true;
					}
				}
				return true;
			} else {
				if (sender.isOp())
					db.alert(sender, db.get("commands-warn-opusage"));
				else {
					db.alert(sender, db.get("commands-warn-usage"));
				}
				return true;
			}
		}
		return true;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		releaseUserInfo(event.getPlayer());
	}
	@SuppressWarnings("serial")
	private void releaseUserInfo(Player player) {
		db.getDB("userinfo").set(player.getName().toLowerCase(), new ConfigSection(new LinkedHashMap<String, Object>() {
			{
				put("ip", player.getAddress());
				put("clientid", plugin.getClientId(player));
			}
		}));
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (BanManager.getInstance().isBanned(player)) {
			event.setKickMessage(db.get("you-are-banned").replace("%reason", BanManager.getInstance().getReason(player.getAddress(), plugin.getClientId(player))));
			event.setCancelled();
			Map<String, Object> info = BanManager.getInstance().getInfoByIP(player.getAddress());
			if (info == null) {
				info = BanManager.getInstance().getInfoByClientId(plugin.getClientId(player));
			}
			info.put("ip", player.getAddress());
			info.put("clientid", plugin.getClientId(player));
		}
	}
}
