package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;

import net.ess3.api.events.AfkStatusChangeEvent;

public class TopSurvivorListener implements Listener {
	
	/* Class Variable */
	
	private IEssentials ess;
	
	/* Events */
	
	// Player Login
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		// Set player scoreboard
		((Player) event).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
	
	// Player Quit
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Log AFK time if player is still AFK when disconnecting
		
	}
	
	// Player AFK Status Change
	@EventHandler
    public void onAFKChange(AfkStatusChangeEvent event) {
		final User user = ess.getUser(((Player) event).getEntityId());
		if (user.isAfk()){
			
		}
		else {
			
		}
		
    }
	
	// Update Time Survived Objective
	@EventHandler
    public void updateTSTime(TopSurvivorUpdate event) {
		for(Player p: Bukkit.getServer().getOnlinePlayers()) {
			Score exempt = TopSurvivor.survivorexemptobjective.getScore(p);
			if(exempt.getScore() == 0){
				Score afktime = TopSurvivor.afktimeobjective.getScore(p);
				Score timesincedeath = TopSurvivor.timesincedeathobjective.getScore(p);
				Score survivortime = TopSurvivor.survivorexemptobjective.getScore(p);
				int current = (int)Math.floor((timesincedeath.getScore() - afktime.getScore())/24000);
				if(survivortime.getScore() < current){
					survivortime.setScore(current);
				}
			}
		}
	}
}
