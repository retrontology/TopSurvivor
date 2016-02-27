package com.retrontology.topsurvivor;

import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;


public class TopSurvivor extends JavaPlugin implements Listener {
	
	/* Class variables */
	
	// Scoreboard
	public static ScoreboardManager tsmanager;
	public static Scoreboard tsboard;
	public static Objective survivortimeobjective;		// Days
	public static Objective timesincedeathobjective;	// Ticks
	public static Objective totalafktimeobjective;	// Ticks
	
	// Plugins/Server
	public static Server server;
	public static TopSurvivorHashMap tshashmap;
	
	// Events
	private TopSurvivorUpdate tsupdate = new TopSurvivorUpdate();
	

	
	/* Init */
	
	
	/* Startup */
	
	
	@Override
	public void onEnable() {
		// Store plugin and server
		server = getServer();
		
		// Init Hashmaps
		tshashmap = new TopSurvivorHashMap(this);
		
		// Create Scoreboard
		makeScoreboard();
		
		// Init online players
		for(Player p: server.getOnlinePlayers()) { initPlayer(p); }
		
		// Register Events
		server.getPluginManager().registerEvents(new TopSurvivorListener(this), this);
		
		// Register Update Scheduler to run every 24000 ticks/1 day
		BukkitScheduler scheduler = server.getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new TopSurvivorTask(this), 0L, 24000L);
		// Register AFKTerminator Scheduler to run every 1200 ticks/1 minute
		scheduler.scheduleSyncRepeatingTask(this, new TopSurvivorAFKTUpdateTask(this), 0L, 1200L);
		
		// Register Commands !NEED TO FIX!
		TopSurvivorCommandExecutor tscommandexec = new TopSurvivorCommandExecutor(this);
		this.getCommand("topsurvivor").setExecutor(tscommandexec);
		
	}
	
	/* Shutdown */
	
	@Override
	public void onDisable() {
		
		// Clean up players
		for(Player p: server.getOnlinePlayers()) {
			// Clean HashMaps
			tshashmap.onLeave(p);
			// Set Player Scores
			refreshPlayer(p);
		}
		// Update all online player before going down
		Bukkit.getPluginManager().callEvent(tsupdate);
	}
	
	
	/* Class Functions */
	
	// Make Scoreboard
	public void makeScoreboard() {
		
		// Grab current main scoreboard
		tsmanager = server.getScoreboardManager();
		tsboard = tsmanager.getMainScoreboard();
		
		// Check to see if Objectives exist and store them. If not, initiate them
		if((totalafktimeobjective = tsboard.getObjective("totalafktime")) == null){
			totalafktimeobjective = tsboard.registerNewObjective("totalafktime", "dummy");
		}
		if(!(totalafktimeobjective.getDisplayName().equals("Top AFKers (Ticks)"))){
			totalafktimeobjective.setDisplayName("Top AFKers (Ticks)");
		}
		if((survivortimeobjective = tsboard.getObjective("survivortime")) == null){
			survivortimeobjective = tsboard.registerNewObjective("survivortime", "dummy");
		}
		if(!(survivortimeobjective.getDisplayName().equals("Top Survivors (Days)"))){
			survivortimeobjective.setDisplayName("Top Survivors (Days)");
		}
		if((timesincedeathobjective = tsboard.getObjective("timesincedeath")) == null){
			timesincedeathobjective = tsboard.registerNewObjective("timesincedeath", "stat.timeSinceDeath");
		}
				
		// Set survivor time to sidebar
		survivortimeobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	// Reset Scoreboard
	public void resetScoreboard() {
		// Finalize online player
		for(Player p: server.getOnlinePlayers()) {
			TopSurvivorPlayer tsplayer = tshashmap.getTopSurvivorPlayer(p);
			// Add final afktime to totalafktime
			totalafktimeobjective.getScore(p).setScore(totalafktimeobjective.getScore(p).getScore() + tsplayer.getCurrentAfkTime());
			// Mark final time
			refreshPlayer(p);
		}
		
		// Find Winners
		List<OfflinePlayer> topsurvivors = getSortedList();
		for(OfflinePlayer player : topsurvivors){ 
			TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
			server.getLogger().info(player.getName() + ": " + (tsp.getTopTick() - tsp.getTopAfkTime() - tsp.getCurrentAfkTPenalty()));
		}
		
		// Distribute Prizes
		
		
		// Reset Objectives
		for(OfflinePlayer player : tsboard.getPlayers())
		{
			tshashmap.getTopSurvivorPlayer(player).reset();
			survivortimeobjective.getScore(player).setScore(0);
			totalafktimeobjective.getScore(player).setScore(0);
			timesincedeathobjective.getScore(player).setScore(0);
		}
	}
	
	// View Scoreboard
	public boolean viewScoreboard(Player player, int page) {
		// Get Players
		List<OfflinePlayer> topsurvivors = getSortedList();
		// Get max pages
		int pagemax = topsurvivors.size();
		pagemax = (pagemax % 10 == 0) ? pagemax/10: pagemax/10+1;
		// If requested page number is out of limits, tell the executor
		if(page > pagemax){ return false; }
		// Send player the Leaderboard
		int offset = (page-1)*10;
		player.sendMessage("---- Top Survivors -- Page " + page + "/" + pagemax + " ----");
		for(int i = offset; i < (10+offset) && i < topsurvivors.size(); i++){
			player.sendMessage((i+1) + ". " + topsurvivors.get(i).getName());
		}
		return true;
	}
	
	// View detailed player data
	public boolean viewPlayer(Player player, String requestedplayer) {
		player.sendMessage("View Player");
		return true;
	}
	
	// Get Sorted List of Players
	public List<OfflinePlayer> getSortedList(){
		Set<OfflinePlayer> topsurvivorset = tsboard.getPlayers();
		for(OfflinePlayer player : topsurvivorset){
			if(!tshashmap.getTopSurvivorPlayer(player).getFlagExempt()){ topsurvivorset.remove(player); }
		}
		List<OfflinePlayer> topsurvivors = new ArrayList<OfflinePlayer>(topsurvivorset);
		Collections.sort(topsurvivors, new TopSurvivorComparator());
		return topsurvivors;
	}
	
	// Init player
	public void initPlayer(Player player){
		TopSurvivorPlayer tsplayer = tshashmap.getTopSurvivorPlayer(player);
		player.setScoreboard(tsboard);
		
		// Look to see if player has been initiated yet
		if(tsplayer.getFlagNew()){
			// Init player scores
			TopSurvivor.survivortimeobjective.getScore(player).setScore(0);
			TopSurvivor.totalafktimeobjective.getScore(player).setScore(0);
			TopSurvivor.timesincedeathobjective.getScore(player).setScore(0);
			tsplayer.setFlagNew(false);
		}
	}
		
	// Update Player Scores
	public void refreshPlayer(Player player){
		TopSurvivorPlayer tsplayer = tshashmap.getTopSurvivorPlayer(player);
		if(!tsplayer.getFlagExempt()){
			Score timesincedeath = timesincedeathobjective.getScore(player);
			if((timesincedeath.getScore() - tsplayer.getCurrentAfkTime()) > (tsplayer.getTopTick() - tsplayer.getTopAfkTime())){
				tsplayer.setTopTick(timesincedeath.getScore());
				tsplayer.setTopAfkTime(tsplayer.getCurrentAfkTime());
			}
			int currentdays = (int)Math.floor((tsplayer.getTopTick() - tsplayer.getTopAfkTime() - tsplayer.getCurrentAfkTPenalty())/24000);
			survivortimeobjective.getScore(player).setScore(currentdays);
		}
	}
	
}
