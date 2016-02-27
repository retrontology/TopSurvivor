package com.retrontology.topsurvivor;

import java.util.HashMap;

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
	public static HashMap<Player, TopSurvivorPlayer> tsplayers = new HashMap<Player, TopSurvivorPlayer>();

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
		// I'm unsure of this logic. Look here if there's a problem with essentials afk time registering
		if (!user.isAfk()){
			// Store Timestamp in map
			timestampmap.put(user.getName(), TopSurvivor.timesincedeathobjective.getScore(player).getScore());
			essentialsafkmap.put(user.getName(), true);
		}else if (essentialsafkmap.get(user.getName()) != null){
			// Add to AFK Time Objective
			Score afkscore = TopSurvivor.afktimeobjective.getScore(player);
			Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
			int timestamp = timestampmap.get(player.getName());
			afkscore.setScore(afkscore.getScore() + (timesincedeathscore.getScore() - timestamp));
			//TopSurvivor.afktimeobjective.getScore(player).setScore(TopSurvivor.afktimeobjective.getScore(player).getScore() + (TopSurvivor.timesincedeathobjective.getScore(player).getScore() - timestampmap.get(user.getName())));
			// Clear Hashmaps
			timestampmap.remove(user.getName());
			essentialsafkmap.remove(user.getName());
		}
	}
	
	public void onAFKTerminator(Player p){
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
			TopSurvivor.afktimeobjective.getScore(p.getName()).setScore(TopSurvivor.afktimeobjective.getScore(p.getName()).getScore() + time);
			TopSurvivor.afktpenaltyobjective.getScore(p).setScore(TopSurvivor.afktpenaltyobjective.getScore(p).getScore() + afktpenalty);
		}
	}
	
	// Triggers on Leave
	public void onLeave(PlayerQuitEvent event){
		Player player = event.getPlayer();
		// Clean Essentials
		if(essentialsafkmap.get(player.getName()) != null){
			// Add to AFK Time Objective
			Score afkscore = TopSurvivor.afktimeobjective.getScore(player);
			Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
			int timestamp = timestampmap.get(player.getName());
			afkscore.setScore(afkscore.getScore() + (timesincedeathscore.getScore() - timestamp));
			//TopSurvivor.afktimeobjective.getScore(player).setScore(TopSurvivor.afktimeobjective.getScore(player).getScore() + (TopSurvivor.timesincedeathobjective.getScore(player).getScore() - timestampmap.get(player.getName())));
			// Clear HashMaps
			timestampmap.remove(player.getName());
			essentialsafkmap.remove(player.getName());
		}
		// Clean AFKTerminator
		if(afkttimestampmap.get(player.getName()) != null){
			int time = TopSurvivor.timesincedeathobjective.getScore(player).getScore() - afkttimestampmap.remove(player.getName());
			TopSurvivor.afktimeobjective.getScore(player.getName()).setScore(TopSurvivor.afktimeobjective.getScore(player.getName()).getScore() + time);
			TopSurvivor.afktpenaltyobjective.getScore(player).setScore(TopSurvivor.afktpenaltyobjective.getScore(player).getScore() + afktpenalty);
		}
	}
	
	// Trigger on 
	public void onDeath(Player player){
		// Clean Essentials
		if(essentialsafkmap.get(player.getName()) != null){
			Score afkscore = TopSurvivor.afktimeobjective.getScore(player);
			Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
			int timestamp = timestampmap.get(player.getName());
			afkscore.setScore(afkscore.getScore() + (timesincedeathscore.getScore() - timestamp));
			// Clear HashMaps
			timestampmap.remove(player.getName());
			essentialsafkmap.remove(player.getName());
		}
		// Clean AFKTerminator
		if(afkttimestampmap.get(player.getName()) != null){
			int time = TopSurvivor.timesincedeathobjective.getScore(player).getScore() - afkttimestampmap.remove(player.getName());
			TopSurvivor.afktimeobjective.getScore(player.getName()).setScore(TopSurvivor.afktimeobjective.getScore(player.getName()).getScore() + time);
			TopSurvivor.afktpenaltyobjective.getScore(player).setScore(TopSurvivor.afktpenaltyobjective.getScore(player).getScore() + afktpenalty);
		}
	}
	
	/* Player HashMap Methods */
	
	// Get TopSurvivorPlayer
	public TopSurvivorPlayer getTopSurvivorPlayer(Player player){
		TopSurvivorPlayer tsp = null;
		if(tsplayers.get(player) == null){
			tsp = tsplayers.put(player, new TopSurvivorPlayer(player.getName(), plugin));
		}else{
			tsp = tsplayers.get(player);
		}
		return tsp;
	}
	
	// Remove TopSurvivorPlayer
	public TopSurvivorPlayer removeTopSurvivorPlayer(Player player){
		return tsplayers.remove(player.getName());
	}
	
	// Get TopSurvivor Player
}
