package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
				for(int i = 0; i < args[2].length(); i++){
					// Signal for player info if not a number
					if(Character.getNumericValue(args[2].charAt(i)) > 39 || Character.getNumericValue(args[2].charAt(i)) < 30){
						page = false;
						break;
					}else{
						// Move number left
						pagenumber *= 10;
						// Add next digit
						pagenumber += (Character.getNumericValue(args[2].charAt(i)) - 30);
					}
				}
				// View additional scoreboard pages
				if(page){
					return plugin.viewScoreboard(player, pagenumber);
				// View detailed info of player	
				}else if(player.hasPermission("topsurvivor.admin")){
					return plugin.viewPlayer(player, args[2]);
				}
			}
			
		}
		return false;
	}
	
}
