package com.retrontology.topsurvivor;

import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	public static Objective afktimeobjective;			// Ticks
	public static Objective afktpenaltyobjective;		// Ticks
	public static Objective survivortimeobjective;		// Days
	public static Objective timesincedeathobjective;	// Ticks
	public static Objective survivorexemptobjective;	// Flag
	public static Objective totalafktimeobjective;		// Ticks
	public static Objective topafktimeobjective;		// Ticks
	public static Objective toptickobjective;			// Ticks
	
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
		for(Player p: server.getOnlinePlayers()) {
			// Set player scoreboard
			p.setScoreboard(tsboard);
			if(survivorexemptobjective.getScore(p).getScore() == 0){
				survivorexemptobjective.getScore(p).setScore(0);
				survivortimeobjective.getScore(p).setScore(0);
				afktimeobjective.getScore(p).setScore(0);
				totalafktimeobjective.getScore(p).setScore(0);
				afktpenaltyobjective.getScore(p).setScore(0);
				topafktimeobjective.getScore(p).setScore(0);
				toptickobjective.getScore(p).setScore(0);
			}
		}
		
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
			tshashmap.onDeath(p);
			// Set Player Scores
			Score exempt = survivorexemptobjective.getScore(p);
			if(exempt.getScore() == 1){
				Score afktime = afktimeobjective.getScore(p);
				Score timesincedeath = timesincedeathobjective.getScore(p);
				if((timesincedeath.getScore() - afktime.getScore()) > toptickobjective.getScore(p).getScore()){
					toptickobjective.getScore(p).setScore(timesincedeath.getScore());
					topafktimeobjective.getScore(p).setScore(afktime.getScore());
				}
				int currentdays = (int)Math.floor((toptickobjective.getScore(p).getScore() - topafktimeobjective.getScore(p).getScore() - afktpenaltyobjective.getScore(p).getScore())/24000);
				survivortimeobjective.getScore(p).setScore(currentdays);
			}
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
		if((afktimeobjective = tsboard.getObjective("afktime")) == null){
			afktimeobjective = tsboard.registerNewObjective("afktime", "dummy");
		}
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
		if((survivorexemptobjective = tsboard.getObjective("survivorexempt")) == null){
			survivorexemptobjective = tsboard.registerNewObjective("survivorexempt", "dummy");
		}
		if((timesincedeathobjective = tsboard.getObjective("timesincedeath")) == null){
			timesincedeathobjective = tsboard.registerNewObjective("timesincedeath", "stat.timeSinceDeath");
		}
		if((afktpenaltyobjective = tsboard.getObjective("afktpenalty")) == null){
			afktpenaltyobjective = tsboard.registerNewObjective("afktpenalty", "dummy");
		}
		if((topafktimeobjective = tsboard.getObjective("topafktime")) == null){
			topafktimeobjective = tsboard.registerNewObjective("topafktime", "dummy");
		}
		if((toptickobjective = tsboard.getObjective("toptick")) == null){
			toptickobjective = tsboard.registerNewObjective("toptick", "dummy");
		}
				
		// Set survivor time to sidebar
		survivortimeobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	// Reset Scoreboard
	public void resetScoreboard() {
		// Finalize online player
		for(Player p: server.getOnlinePlayers()) {
			// Add final afktime to totalafktime
			totalafktimeobjective.getScore(p).setScore(totalafktimeobjective.getScore(p).getScore() + afktimeobjective.getScore(p).getScore());
			// Mark final time
			Score exempt = survivorexemptobjective.getScore(p);
			if(exempt.getScore() == 1){
				Score afktime = afktimeobjective.getScore(p);
				Score timesincedeath = timesincedeathobjective.getScore(p);
				if((timesincedeath.getScore() - afktime.getScore()) > (toptickobjective.getScore(p).getScore() - topafktimeobjective.getScore(p).getScore())){
					toptickobjective.getScore(p).setScore(timesincedeath.getScore());
					topafktimeobjective.getScore(p).setScore(afktime.getScore());
				}
				int currentdays = (int)Math.floor((toptickobjective.getScore(p).getScore() - topafktimeobjective.getScore(p).getScore() - afktpenaltyobjective.getScore(p).getScore())/24000);
				survivortimeobjective.getScore(p).setScore(currentdays);
			}
		}
		
		// Find Winners
		List<OfflinePlayer> topsurvivors = getSortedList();
		for(OfflinePlayer player : topsurvivors){ 
			server.getLogger().info(player.getName() + ": " + (toptickobjective.getScore(player).getScore() - topafktimeobjective.getScore(player).getScore() - afktpenaltyobjective.getScore(player).getScore()));
		}
		
		// Distribute Prizes
		
		
		// Reset Objectives
		for(OfflinePlayer player : tsboard.getPlayers())
		{
			if(survivorexemptobjective.getScore(player).getScore() == 1){ survivorexemptobjective.getScore(player).setScore(1); }
			survivortimeobjective.getScore(player).setScore(0);
			afktimeobjective.getScore(player).setScore(0);
			totalafktimeobjective.getScore(player).setScore(0);
			afktpenaltyobjective.getScore(player).setScore(0);
			topafktimeobjective.getScore(player).setScore(0);
			toptickobjective.getScore(player).setScore(0);
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
		player.sendMessage(ChatColor.AQUA + "View Player");
		return true;
	}
	
	// Get Sorted List of Players
	public List<OfflinePlayer> getSortedList(){
		Set<OfflinePlayer> topsurvivorset = tsboard.getPlayers();
		for(OfflinePlayer player : topsurvivorset){
			if(survivorexemptobjective.getScore(player).getScore() != 1){ topsurvivorset.remove(player); }
		}
		List<OfflinePlayer> topsurvivors = new ArrayList<OfflinePlayer>(topsurvivorset);
		Collections.sort(topsurvivors, new TopSurvivorComparator());
		return topsurvivors;
	}
	
}
