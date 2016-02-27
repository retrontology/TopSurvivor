package com.retrontology.topsurvivor;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Score;

import com.earth2me.essentials.IUser;

import me.edge209.afkTerminator.AfkTerminatorAPI;
import net.ess3.api.events.AfkStatusChangeEvent;

public class TopSurvivorHashMap {
	
	/* Class Variables */
	
	private TopSurvivor plugin;
	private int afktpenalty;
	
	// AFK HashMaps
	public static HashMap<String, Integer> timestampmap = new HashMap<String, Integer>();
	public static HashMap<String, Integer> afkttimestampmap = new HashMap<String, Integer>();
	public static HashMap<String, Boolean> essentialsafkmap = new HashMap<String, Boolean>();
	
	// Player HashMap
	public static HashMap<String, TopSurvivorPlayer> tsplayers = new HashMap<String, TopSurvivorPlayer>();

	/* Constructor */
	
	public TopSurvivorHashMap(TopSurvivor plugin){
		this.plugin = plugin;
		// Initialize AFKTerminator Penalty
		afktpenalty = 2400;
	}
	
	/* Interfaces */
	
	// Triggers on AFKStatusChangeEvent
	public void onChangeEssentialsAfk(AfkStatusChangeEvent event){
		IUser user = event.getAffected();
		Player player = TopSurvivor.server.getPlayer(user.getName());
		TopSurvivorPlayer tsplayer = tsplayers.get(player);
		// I'm unsure of this logic. Look here if there's a problem with essentials afk time registering
		if (!user.isAfk()){
			// Store Timestamp in map
			timestampmap.put(user.getName(), TopSurvivor.timesincedeathobjective.getScore(player).getScore());
			essentialsafkmap.put(user.getName(), true);
		}else if (essentialsafkmap.get(user.getName()) != null){
			// Add to AFK Time Objective
			Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
			int timestamp = timestampmap.get(player.getName());
			tsplayer.setCurrentAfkTime(tsplayer.getCurrentAfkTime() + (timesincedeathscore.getScore() - timestamp));
			//TopSurvivor.afktimeobjective.getScore(player).setScore(TopSurvivor.afktimeobjective.getScore(player).getScore() + (TopSurvivor.timesincedeathobjective.getScore(player).getScore() - timestampmap.get(user.getName())));
			// Clear Hashmaps
			timestampmap.remove(user.getName());
			essentialsafkmap.remove(user.getName());
		}
	}
	
	public void onAFKTerminator(Player p){
		TopSurvivorPlayer tsplayer = tsplayers.get(p);
		// See if they're using an afk machine
		if(AfkTerminatorAPI.isAFKMachineDetected(p.getName())){
			// Initialize Player if they're not already detected
			if(afkttimestampmap.get(p.getName()) == null){
				// Time stamp
				afkttimestampmap.put(p.getName(), TopSurvivor.timesincedeathobjective.getScore(p).getScore());
		}
		// Store AFK Terminator Time
		}else if(afkttimestampmap.get(p.getName()) != null){
			int time = TopSurvivor.timesincedeathobjective.getScore(p).getScore() - afkttimestampmap.remove(p.getName());
			tsplayer.setCurrentAfkTime(tsplayer.getCurrentAfkTime() + time);
			tsplayer.setCurrentAfkTPenalty(tsplayer.getCurrentAfkTPenalty() + afktpenalty);
		}
	}
	
	// Triggers on Leave
	public void onLeave(Player player){
		TopSurvivorPlayer tsplayer = tsplayers.get(player);
		// Clean Essentials
		if(essentialsafkmap.get(player.getName()) != null){
			// Add to AFK Time Objective
			Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
			int timestamp = timestampmap.get(player.getName());
			tsplayer.setCurrentAfkTime(tsplayer.getCurrentAfkTime() + (timesincedeathscore.getScore() - timestamp));
			//TopSurvivor.afktimeobjective.getScore(player).setScore(TopSurvivor.afktimeobjective.getScore(player).getScore() + (TopSurvivor.timesincedeathobjective.getScore(player).getScore() - timestampmap.get(user.getName())));
			// Clear Hashmaps
			timestampmap.remove(player.getName());
			essentialsafkmap.remove(player.getName());
		}
		// Clean AFKTerminator
		if(afkttimestampmap.get(player.getName()) != null){
			int time = TopSurvivor.timesincedeathobjective.getScore(player).getScore() - afkttimestampmap.remove(player.getName());
			tsplayer.setCurrentAfkTime(tsplayer.getCurrentAfkTime() + time);
			tsplayer.setCurrentAfkTPenalty(tsplayer.getCurrentAfkTPenalty() + afktpenalty);
		}
		// Clean and save player
		removeTopSurvivorPlayer(player);
	}
	
	// Trigger on Death
	public void onDeath(Player player){
		TopSurvivorPlayer tsplayer = tsplayers.get(player);
		// Clean Essentials
		if(essentialsafkmap.get(player.getName()) != null){
			// Add to AFK Time Objective
			Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
			int timestamp = timestampmap.get(player.getName());
			tsplayer.setCurrentAfkTime(tsplayer.getCurrentAfkTime() + (timesincedeathscore.getScore() - timestamp));
			//TopSurvivor.afktimeobjective.getScore(player).setScore(TopSurvivor.afktimeobjective.getScore(player).getScore() + (TopSurvivor.timesincedeathobjective.getScore(player).getScore() - timestampmap.get(user.getName())));
			// Clear Hashmaps
			timestampmap.remove(player.getName());
			essentialsafkmap.remove(player.getName());
		}
		// Clean AFKTerminator
		if(afkttimestampmap.get(player.getName()) != null){
			int time = TopSurvivor.timesincedeathobjective.getScore(player).getScore() - afkttimestampmap.remove(player.getName());
			tsplayer.setCurrentAfkTime(tsplayer.getCurrentAfkTime() + time);
			tsplayer.setCurrentAfkTPenalty(tsplayer.getCurrentAfkTPenalty() + afktpenalty);
		}
	}
	
	/* Player HashMap Methods */
	
	// Get TopSurvivorPlayer
	public TopSurvivorPlayer getTopSurvivorPlayer(Player player){
		TopSurvivorPlayer tsp = null;
		if(tsplayers.get(player.getName()) == null){
			tsp = tsplayers.put(player.getName(), new TopSurvivorPlayer(player.getName(), plugin));
		}else{
			tsp = tsplayers.get(player);
		}
		return tsp;
	}
	public TopSurvivorPlayer getTopSurvivorPlayer(OfflinePlayer player){
		TopSurvivorPlayer tsp = null;
		if(tsplayers.get(player.getName()) == null){
			tsp = tsplayers.put(player.getName(), new TopSurvivorPlayer(player.getName(), plugin));
		}else{
			tsp = tsplayers.get(player);
		}
		return tsp;
	}
	public TopSurvivorPlayer getTopSurvivorPlayer(String player){
		TopSurvivorPlayer tsp = null;
		if(tsplayers.get(player) == null){
			tsp = tsplayers.put(player, new TopSurvivorPlayer(player, plugin));
		}else{
			tsp = tsplayers.get(player);
		}
		return tsp;
	}
	
	// Remove TopSurvivorPlayer
	public TopSurvivorPlayer removeTopSurvivorPlayer(Player player){
		tsplayers.get(player).save();
		return tsplayers.remove(player.getName());
	}

}
