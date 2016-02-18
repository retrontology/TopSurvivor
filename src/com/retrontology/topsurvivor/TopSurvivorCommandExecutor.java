package com.retrontology.topsurvivor;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopSurvivorCommandExecutor implements CommandExecutor {
	
	/* Class Variables */
	
	private TopSurvivor plugin;
	
	public TopSurvivorCommandExecutor(TopSurvivor plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("topsurvivor reset")) {
			player.sendMessage(ChatColor.AQUA + "Reset");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("topsurvivor view")) {
			player.sendMessage(ChatColor.AQUA + "View");
			return true;
		}
		return false;
	}
	
}
