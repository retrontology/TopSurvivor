package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Score;

import com.earth2me.essentials.IUser;

import net.ess3.api.events.AfkStatusChangeEvent;
import me.edge209.afkTerminator.AfkTerminator;
import me.edge209.afkTerminator.AfkTerminatorAPI;

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
            	// Make sure player is actually logged on
            	if(player.isOnline()){
            		player.setScoreboard(TopSurvivor.tsboard);
            		
            		// Look to see if player has been initiated yet
            		if(TopSurvivor.survivorexemptobjective.getScore(player).getScore() == 0){
            			// Init player scores
            			TopSurvivor.survivorexemptobjective.getScore(player).setScore(1);
            			TopSurvivor.survivortimeobjective.getScore(player).setScore(0);
            			TopSurvivor.afktimeobjective.getScore(player).setScore(0);
            			TopSurvivor.totalafktimeobjective.getScore(player).setScore(0);
            		}
        		}
            }
        }, 10L);
		
	}
	
	// Player Quit
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Log AFK time if player is still AFK when disconnecting
		Player player = event.getPlayer();
		TopSurvivor.tshashmap.onLeave(event);
		if(TopSurvivor.survivorexemptobjective.getScore(player).getScore() == 1){
			Score afktime = TopSurvivor.afktimeobjective.getScore(player);
			Score timesincedeath = TopSurvivor.timesincedeathobjective.getScore(player);
			Score survivortime = TopSurvivor.survivortimeobjective.getScore(player);
			int current = (int)Math.floor((timesincedeath.getScore() - afktime.getScore() - TopSurvivor.afktpenaltyobjective.getScore(player).getScore())/24000);
			if(survivortime.getScore() < current){
				survivortime.setScore(current);
			}
		}
	}
	
	// Player Death
	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		// Finalize data in HashMap and clear it
		TopSurvivor.tshashmap.onDeath(player);
		
		// Mark final time
		if(TopSurvivor.survivorexemptobjective.getScore(player).getScore() == 1){
			Score afktime = TopSurvivor.afktimeobjective.getScore(player);
			Score timesincedeath = TopSurvivor.timesincedeathobjective.getScore(player);
			Score survivortime = TopSurvivor.survivortimeobjective.getScore(player);
			int current = (int)Math.floor((timesincedeath.getScore() - afktime.getScore() - TopSurvivor.afktpenaltyobjective.getScore(player).getScore())/24000);
			if(survivortime.getScore() < current){
				survivortime.setScore(current);
			}
		}
		// Add afktime to totalafktime
		TopSurvivor.totalafktimeobjective.getScore(player).setScore(TopSurvivor.totalafktimeobjective.getScore(player).getScore() + TopSurvivor.afktimeobjective.getScore(player).getScore());
		// Reset Objectives
		TopSurvivor.afktimeobjective.getScore(player).setScore(0);
	}
	
	// Player AFK Status Change
	@EventHandler
    public void onAFKChange(AfkStatusChangeEvent event) {
		// Make sure player is online
		if(TopSurvivor.server.getPlayer(event.getAffected().getName()).isOnline()){
			TopSurvivor.tshashmap.onChangeEssentialsAfk(event);
		}
    }
	
	// Update Time Survived Objective for all online players
	@EventHandler
    public void updateTSTime(TopSurvivorUpdate event) {
		for(Player p: TopSurvivor.server.getOnlinePlayers()) {
			Score exempt = TopSurvivor.survivorexemptobjective.getScore(p);
			if(exempt.getScore() == 1){
				Score afktime = TopSurvivor.afktimeobjective.getScore(p);
				Score timesincedeath = TopSurvivor.timesincedeathobjective.getScore(p);
				Score survivortime = TopSurvivor.survivortimeobjective.getScore(p);
				int current = (int)Math.floor((timesincedeath.getScore() - afktime.getScore() - TopSurvivor.afktpenaltyobjective.getScore(p).getScore())/24000);
				if(survivortime.getScore() < current){
					survivortime.setScore(current);
				}
			}
		}
		TopSurvivor.server.getLogger().info("[Top Survivor] Top Survivors list updated");
	}
	
	// Event to poll for AFKTerminator Integration
	@EventHandler
	public void updateAFKTTime(TopSurvivorAFKTUpdate event){
		for(Player p: TopSurvivor.server.getOnlinePlayers()) {
			TopSurvivor.tshashmap.onAFKTerminator(p);
		}
	}
	
}
