package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Score;

import com.earth2me.essentials.IUser;

import net.ess3.api.events.AfkStatusChangeEvent;

public class TopSurvivorListener implements Listener {
	
	/* Class Variable */
	
	public TopSurvivor plugin;
	
	/* Constructor */

	public TopSurvivorListener(TopSurvivor plugin){
		this.plugin = plugin;
	}
	
	/* Events */
	
	// Player Login
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		// Set player scoreboard;
		final Player player = event.getPlayer();
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
        		player.setScoreboard(TopSurvivor.tsboard);
        		
        		// Look to see if player has been initiated yet
        		if(TopSurvivor.survivorexemptobjective.getScore(player).getScore() == 0){
        			// Init player scores
        			TopSurvivor.survivorexemptobjective.getScore(player).setScore(1);
        			TopSurvivor.survivortimeobjective.getScore(player).setScore(0);
        			TopSurvivor.afktimeobjective.getScore(player).setScore(0);
        		}
            }
        }, 10L);
		
	}
	
	// Player Quit
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Log AFK time if player is still AFK when disconnecting
		
	}
	
	// Player AFK Status Change
	@EventHandler
    public void onAFKChange(AfkStatusChangeEvent event) {
		
		IUser user = event.getAffected(); 
		if (user.isAfk()){
			
		}
		else {
			
		}
		
    }
	
	// Update Time Survived Objective
	@EventHandler
    public void updateTSTime(TopSurvivorUpdate event) {
		for(Player p: TopSurvivor.server.getOnlinePlayers()) {
			Score exempt = TopSurvivor.survivorexemptobjective.getScore(p);
			if(exempt.getScore() == 1){
				Score afktime = TopSurvivor.afktimeobjective.getScore(p);
				Score timesincedeath = TopSurvivor.timesincedeathobjective.getScore(p);
				Score survivortime = TopSurvivor.survivortimeobjective.getScore(p);
				int current = (int)Math.floor((timesincedeath.getScore() - afktime.getScore())/24000);
				if(survivortime.getScore() < current){
					survivortime.setScore(current);
				}
			}
		}
		TopSurvivor.server.getLogger().info("Top Survivors list updated");
	}
}
