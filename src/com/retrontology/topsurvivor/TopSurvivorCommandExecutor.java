package com.retrontology.topsurvivor;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopSurvivorCommandExecutor implements CommandExecutor {
	
	/* Class Variables */
	
	private TopSurvivor plugin;
	private TopSurvivorUpdate tsupdate = new TopSurvivorUpdate();
	
	public TopSurvivorCommandExecutor(TopSurvivor plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("topsurvivor")) {
			// View (Default Action)
			if(args.length == 0 && (player.hasPermission("topsurvivor.citizen")))
			{
				plugin.viewScoreboard(player, 1);
				return true;
			}
			if(args.length == 1){
				// Reset
				if(args[0].equalsIgnoreCase("reset") && (player.hasPermission("topsurvivor.admin"))){
					player.sendMessage(ChatColor.AQUA + "Reset");
					plugin.resetScoreboard();
					return true;
				}
				// Update
				if(args[0].equalsIgnoreCase("update") && (player.hasPermission("topsurvivor.admin"))){
					player.sendMessage(ChatColor.AQUA + "Updated");
					Bukkit.getPluginManager().callEvent(tsupdate);
					return true;
				}
				// View (No offset)
				if(args[0].equalsIgnoreCase("view") && (player.hasPermission("topsurvivor.citizen"))){
					plugin.viewScoreboard(player, 1);
					return true;
				}
			}
			// Detailed View Commands
			if(args.length == 2 && args[0].equalsIgnoreCase("view") && player.hasPermission("topsurvivor.citizen")){
				boolean page = true;
				int pagenumber = 0;
				// Parse argument for number
				for(int i = 0; i < args[1].length(); i++){
					// Signal for player info if not a number
					if(Character.getNumericValue(args[1].charAt(i)) > 9){
						page = false;
						break;
					}else{
						// Move number left
						pagenumber *= 10;
						// Add next digit
						pagenumber += (Character.getNumericValue(args[1].charAt(i)));
					}
				}
				// View additional scoreboard pages
				if(page){
					return plugin.viewScoreboard(player, pagenumber);
				// View detailed info of player	
				}else if(player.hasPermission("topsurvivor.admin") || (args[1].equalsIgnoreCase(player.getName()))){
					return plugin.viewPlayer(player, args[1]);
				}else{ return false; }
			}
			// Temp ban player command
			if(args.length == 2 && args[0].equalsIgnoreCase("tempban") && player.hasPermission("topsurvivor.admin")){
				if(plugin.tempBan(args[1])){
					player.sendMessage(args[1] + " has been banned from the Top Survivor Leaderboard");
					plugin.server.getLogger().info("[Top Survivor] " + args[1] + " has been banned");
					return true;	
				}else{ return false; }
			}
			// Permaban player command
			if(args.length == 2 && args[0].equalsIgnoreCase("permaban") && player.hasPermission("topsurvivor.admin")){
				if(plugin.permaBan(args[1])){
					player.sendMessage(args[1] + " has been permabanned from the Top Survivor Leaderboard. rip");
					plugin.server.getLogger().info("[Top Survivor] " + args[1] + " has been permabanned");
					return true;
				}else{ return false; }
			}
			// Unban player command
			if(args.length == 2 && args[0].equalsIgnoreCase("unban") && player.hasPermission("topsurvivor.admin")){
				if(plugin.unBan(args[1])){
					player.sendMessage(args[1] + " has been unbanned from the Top Survivor Leaderboard");
					plugin.server.getLogger().info("[Top Survivor] " + args[1] + " has been unbanned");
				}else{ return false; }
			}
		}
		return false;
	}
	
}
