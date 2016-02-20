package com.retrontology.topsurvivor;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.edge209.afkTerminator.AfkTerminatorAPI;


public class TopSurvivor extends JavaPlugin implements Listener {
	
	/* Class variables */
	
	// Scoreboard
	public static ScoreboardManager tsmanager;
	public static Scoreboard tsboard;
	public static Objective afktimeobjective;			// Ticks
	public static Objective survivortimeobjective;		// Days
	public static Objective timesincedeathobjective;	// Ticks
	public static Objective survivorexemptobjective;	// Flag
	public static Objective totalafktimeobjective;		// Ticks
	
	// Plugins/Server
	public static Server server;
	private TopSurvivorUpdate tsupdate = new TopSurvivorUpdate();
	public static TopSurvivorHashMap tshashmap;
	
	
	/* Init */
	
	
	/* Startup */
	
	
	@Override
	public void onEnable() {
		// Store plugin and server
		TopSurvivor.server = getServer();
		
		// Init Hashmaps
		tshashmap = new TopSurvivorHashMap(this);
		
		// Create Scoreboard
		makeScoreboard();
		
		// Init online players
		for(Player p: TopSurvivor.server.getOnlinePlayers()) {
			// Set player scoreboard
			p.setScoreboard(tsboard);
			if(survivorexemptobjective.getScore(p).getScore() == 0){
				survivorexemptobjective.getScore(p).setScore(0);
				survivortimeobjective.getScore(p).setScore(0);
				afktimeobjective.getScore(p).setScore(0);
				totalafktimeobjective.getScore(p).setScore(0);
			}
		}
		
		// Register Events
		TopSurvivor.server.getPluginManager().registerEvents(new TopSurvivorListener(this), this);
		
		// Register Scheduler to run every 24000 ticks/1 day
		BukkitScheduler scheduler = TopSurvivor.server.getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new TopSurvivorTask(this), 0L, 24000L);
		
		// Register Commands !NEED TO FIX!
		TopSurvivorCommandExecutor tscommandexec = new TopSurvivorCommandExecutor(this);
		this.getCommand("topsurvivor").setExecutor(tscommandexec);
		
	}
	
	/* Shutdown */
	
	@Override
	public void onDisable() {
		
		// Clean up players
		for(Player p: TopSurvivor.server.getOnlinePlayers()) {
			tshashmap.onDeath(p);
		}
		// Update all online player before going down
		Bukkit.getPluginManager().callEvent(tsupdate);
	}
	
	
	/* Class Functions */
	
	// Make Scoreboard
	public void makeScoreboard() {
		
		// Grab current main scoreboard
		tsmanager = TopSurvivor.server.getScoreboardManager();
		tsboard = tsmanager.getMainScoreboard();
		
		// Check to see if Objectives exist and store them. If not, initiate them
		if((afktimeobjective = tsboard.getObjective("afktime")) == null){
			afktimeobjective = tsboard.registerNewObjective("afktime", "dummy");
		}
		if((totalafktimeobjective = tsboard.getObjective("totalafktime")) == null){
			totalafktimeobjective = tsboard.registerNewObjective("totalafktime", "dummy");
		}
		if(!(totalafktimeobjective.getDisplayName().equals("Top AFKers(Ticks)"))){
			totalafktimeobjective.setDisplayName("Top AFKers(Ticks)");
		}
		if((survivortimeobjective = tsboard.getObjective("survivortime")) == null){
			survivortimeobjective = tsboard.registerNewObjective("survivortime", "dummy");
		}
		if(!(survivortimeobjective.getDisplayName().equals("Top Survivors(Days)"))){
			survivortimeobjective.setDisplayName("Top Survivors(Days)");
		}
		if((survivorexemptobjective = tsboard.getObjective("survivorexempt")) == null){
			survivorexemptobjective = tsboard.registerNewObjective("survivorexempt", "dummy");
		}
		if((timesincedeathobjective = tsboard.getObjective("timesincedeath")) == null){
			timesincedeathobjective = tsboard.registerNewObjective("timesincedeath", "stat.timeSinceDeath");
		}
				
		// Set survivor time to sidebar
		survivortimeobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	// Reset Scoreboard
	public void resetScoreboard() {
		
		// Add final afktime to totalafktime
	}
	
	// View Scoreboard
	public void viewScoreboard(Player player) {
		
	}
	
}
