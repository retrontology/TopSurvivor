package com.retrontology.topsurvivor;

import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
		
		// Needs to wait a bit for player to register, otherwise it will thrown an exception
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
            	// Make sure player is actually logged on
            	if(player.isOnline()){ plugin.initPlayer(player); }
            }
        }, 10L);
		
	}
	
	// Player Quit
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Log AFK time if player is still AFK when disconnecting
		Player player = event.getPlayer();
		// Clean Hashmaps
		TopSurvivor.tshashmap.onLeave(player);
		// Refresh Player
		plugin.refreshPlayer(player);
	}
	
	// Player Death
	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		TopSurvivorPlayer tsplayer = plugin.tshashmap.getTopSurvivorPlayer(player);
		tsplayer.setLastDeath(new Date().getTime());
		// Finalize data in HashMap and clear it
		TopSurvivor.tshashmap.onDeath(player);
		
		// Mark final time
		plugin.refreshPlayer(player);
		// Add afktime to totalafktime
		TopSurvivor.totalafktimeobjective.getScore(player).setScore(TopSurvivor.totalafktimeobjective.getScore(player).getScore() + tsplayer.getCurrentAfkTime());
		// Reset Objectives
		tsplayer.setCurrentAfkTime(0);
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
		for(Player p: TopSurvivor.server.getOnlinePlayers()) { plugin.refreshPlayer(p); }
		List<String> list = plugin.getSortedList();
		plugin.survivortimeobjective.unregister();
		plugin.makeScoreboard();
		for(int i = 0; i < plugin.getDisplayCount() && i < list.size(); i++){
			plugin.survivortimeobjective.getScore(list.get(i)).setScore(plugin.tshashmap.getTopSurvivorPlayer(list.get(i)).getSurvivorTime());
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
