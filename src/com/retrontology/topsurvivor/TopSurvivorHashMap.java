package com.retrontology.topsurvivor;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Score;

import com.earth2me.essentials.IUser;

import net.ess3.api.events.AfkStatusChangeEvent;

public class TopSurvivorHashMap {
	
	/* Class Variables */
	
	private TopSurvivor plugin;
	
	// HashMaps
	public static HashMap<String, Integer> timestampmap = new HashMap<String, Integer>();
	public static HashMap<String, Boolean> essentialsafkmap = new HashMap<String, Boolean>();
	public static HashMap<String, Boolean> afkterminatormap = new HashMap<String, Boolean>();

	/* Constructor */
	
	public TopSurvivorHashMap(TopSurvivor plugin){
		this.plugin = plugin;
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
			// Clear Hashmaps
			timestampmap.remove(player.getName());
			essentialsafkmap.remove(player.getName());
		}
		// Clean AFKTerminator
		
	}
	
	public void onDeath(Player player){
		// Clean Essentials
		if(essentialsafkmap.get(player.getName()) != null){
			Score afkscore = TopSurvivor.afktimeobjective.getScore(player);
			Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
			int timestamp = timestampmap.get(player.getName());
			afkscore.setScore(afkscore.getScore() + (timesincedeathscore.getScore() - timestamp));
			// Clear Hashmaps
			timestampmap.remove(player.getName());
			essentialsafkmap.remove(player.getName());
		}
		// Clean AFKTerminator
		
	}
}
