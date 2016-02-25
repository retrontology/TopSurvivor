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
			// View
			if((args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("view"))) && (player.hasPermission("topsurvivor.citizen")))
			{
				plugin.viewScoreboard(player);
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
			}
			
		}
		return false;
	}
	
}
