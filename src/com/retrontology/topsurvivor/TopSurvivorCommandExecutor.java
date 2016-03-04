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
				if(args[0].equalsIgnoreCase("reset")){
					if(player.hasPermission("topsurvivor.admin")){
						plugin.resetScoreboard();
						player.sendMessage("The Scoreboard has been reset!");
						return true;
					}else{
						player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
						return true;
					}
				}
				// Update
				if(args[0].equalsIgnoreCase("update")){
					if(player.hasPermission("topsurvivor.admin")){
						Bukkit.getPluginManager().callEvent(tsupdate);
						player.sendMessage("The Scoreboard has been updated!");
						return true;
					}
					else{
						player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
						return true;
					}
					
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
				}else{
					player.sendMessage("You did not enter a valid page/player");
					return true; 
				}
			}
			// Temp ban player command
			if(args.length == 2 && args[0].equalsIgnoreCase("tempban")){
				if(player.hasPermission("topsurvivor.admin")){
					if(plugin.tempBan(args[1])){
						player.sendMessage(args[1] + " has been banned from the Top Survivor Leaderboard");
						plugin.server.getLogger().info("[Top Survivor] " + args[1] + " has been banned");
						return true;	
					}else{
						player.sendMessage("You did not enter a valid player");
						return true;
					}
				}else{
					player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
					return true;
				}
			}
			// Permaban player command
			if(args.length == 2 && args[0].equalsIgnoreCase("permaban")){
				if(player.hasPermission("topsurvivor.admin")){
					if(plugin.permaBan(args[1])){
						player.sendMessage(args[1] + " has been permabanned from the Top Survivor Leaderboard. rip");
						plugin.server.getLogger().info("[Top Survivor] " + args[1] + " has been permabanned");
						return true;
					}else{
						player.sendMessage("You did not enter a valid player");
						return true;
					}
				}else{
					player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
					return true;
				}
			}
			// Unban player command
			if(args.length == 2 && args[0].equalsIgnoreCase("unban") && player.hasPermission("topsurvivor.admin")){
				if(player.hasPermission("topsurvivor.admin")){
					if(plugin.unBan(args[1])){
						player.sendMessage(args[1] + " has been unbanned from the Top Survivor Leaderboard");
						plugin.server.getLogger().info("[Top Survivor] " + args[1] + " has been unbanned");
					}else{
						player.sendMessage("You did not enter a valid player");
						return true;
					}
				}else{
					player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
					return true;
				}
			}
			
			// Modify player's AFKTerminator Penalty
			if(args[0].equalsIgnoreCase("afktpenalty")){
				if(player.hasPermission("topsurvivor.admin")){
					if(args.length>1){
						// Add to AFKTerminator Penalty
						if(args[1].equalsIgnoreCase("add")){
							if(args.length == 4){
								int multiplier = 0;
								for(int i = 0; i < args[3].length(); i++){
									// Signal for player info if not a number
									if(Character.getNumericValue(args[3].charAt(i)) > 9){
										player.sendMessage("Please enter a valid integer");
										return true;
									}else{
										// Move number left
										multiplier *= 10;
										// Add next digit
										multiplier += (Character.getNumericValue(args[3].charAt(i)));
									}
								}
								if(plugin.afkTerminatoryPenaltyAdd(args[2], multiplier)){
									player.sendMessage(args[2] + " has had " + (multiplier*plugin.getAFKTerminatorPenalty()) + " ticks added to their AFKTerminatorPenalty and now has a penalty of: " + plugin.tshashmap.getTopSurvivorPlayer(args[2]).getCurrentAfkTPenalty());
									plugin.server.getLogger().info("[Top Survivor] " + args[2] + " has had " + (multiplier*plugin.getAFKTerminatorPenalty()) + " ticks added to their AFKTerminatorPenalty and now has a penalty of: " + plugin.tshashmap.getTopSurvivorPlayer(args[2]).getCurrentAfkTPenalty());
									return true;
								}else{
									player.sendMessage("Please enter a valid player");
								}
							}else{
								player.sendMessage("Usage: ");
								player.sendMessage("/topsurvivor afktpenalty add <player> <multiplier>");
								return true;
							}
						}
						// Remove from AFKTerminator Penalty
						if(args[1].equalsIgnoreCase("remove")){
							if(args.length == 4){
								int multiplier = 0;
								for(int i = 0; i < args[3].length(); i++){
									// Signal for player info if not a number
									if(Character.getNumericValue(args[3].charAt(i)) > 9){
										player.sendMessage("Please enter a valid integer");
										return true;
									}else{
										// Move number left
										multiplier *= 10;
										// Add next digit
										multiplier += (Character.getNumericValue(args[3].charAt(i)));
									}
								}
								if(plugin.afkTerminatoryPenaltyRemove(args[2], multiplier)){
									player.sendMessage(args[2] + " has had " + (multiplier*plugin.getAFKTerminatorPenalty()) + " ticks removed from their AFKTerminatorPenalty and now has a penalty of: " + plugin.tshashmap.getTopSurvivorPlayer(args[2]).getCurrentAfkTPenalty());
									plugin.server.getLogger().info("[Top Survivor] " + args[2] + " has had " + (multiplier*plugin.getAFKTerminatorPenalty()) + "ticks added to their AFKTerminatorPenalty and now has a penalty of: " + plugin.tshashmap.getTopSurvivorPlayer(args[2]).getCurrentAfkTPenalty());
									return true;
								}else{
									player.sendMessage("Please enter a valid player");
								}
							}else{
								player.sendMessage("Usage: ");
								player.sendMessage("/topsurvivor afktpenalty remove <player> <multiplier>");
								return true;
							}
						}
						// Clear a players AFKTerminator penalty
						if(args[1].equalsIgnoreCase("clear")){
							if(args.length == 3){
								if(plugin.afkTerminatoryPenaltyClear(args[2])){
									player.sendMessage(args[2] + " has had their AFKTerminator penalty cleared");
									plugin.server.getLogger().info("[Top Survivor] " + args[2] + " has had their AFKTerminator penalty cleared");
								}else{
									
								}
							}else{
								player.sendMessage("Usage: ");
								player.sendMessage("/topsurvivor afktpenalty clear <player>");
								return true;
							}
						}
						// Set the AFKTerminator penalty in the config
						if(args[1].equalsIgnoreCase("set")){
							if(args.length == 3){
								int ticks = 0;
								for(int i = 0; i < args[2].length(); i++){
									// Signal for player info if not a number
									if(Character.getNumericValue(args[2].charAt(i)) > 9){
										player.sendMessage("Please enter a valid integer");
										return true;
									}else{
										// Move number left
										ticks *= 10;
										// Add next digit
										ticks += (Character.getNumericValue(args[2].charAt(i)));
									}
								}
								if(plugin.setAFKTerminatorPenalty(ticks)){
									player.sendMessage("The AFKTerminator penalty has been set to " + ticks + " ticks");
									plugin.server.getLogger().info("[Top Survivor] " + "The AFKTerminator penalty has been set to " + ticks + " ticks");
									return true;
								}else{
									player.sendMessage("The config file could not be saved for some rease :/ check the console/log for a stacktrace");
									return true;
								}
							}else{
								player.sendMessage("Usage: ");
								player.sendMessage("/topsurvivor afktpenalty set <ticks>");
								return true;
							}
						}
						
					}else{
						player.sendMessage("Usage: ");
						player.sendMessage("/topsurvivor afktpenalty add <player> <multiplier>");
						player.sendMessage("/topsurvivor afktpenalty remove <player> <multiplier>");
						player.sendMessage("/topsurvivor afktpenalty clear <player>");
						player.sendMessage("/topsurvivor afktpenalty set <ticks>");
						return true;
					}
				}else{
					player.sendMessage("What do you think you are doing :I");
					return true;
				}
			}
		}
		return false;
	}
	
}
