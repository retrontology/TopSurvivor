package com.retrontology.topsurvivor;

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
	
	public static ScoreboardManager tsmanager;
	public static Scoreboard tsboard;
	public static Objective afktimeobjective;			// Ticks
	public static Objective survivortimeobjective;		// Days
	public static Objective timesincedeathobjective;	// Ticks
	public static Objective survivorexemptobjective;	// Flag
	
	private Plugin plugin;
	public static Server server;
	
	
	/* Init */
	
	
	/* Startup */
	
	
	@Override
	public void onEnable() {
		// Store plugin and server
		plugin = this;
		TopSurvivor.server = getServer();
		
		// Create Scoreboard
		makeScoreboard();
		
		// Init online players
		for(Player p: TopSurvivor.server.getOnlinePlayers()) {
			// Set player scoreboard
			p.setScoreboard(tsboard);
		}
		
		// Register Events
		TopSurvivor.server.getPluginManager().registerEvents(new TopSurvivorListener(this), this);
		
		// Register Scheduler to run every 24000 ticks/1 day
		BukkitScheduler scheduler = TopSurvivor.server.getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new TopSurvivorTask(this), 0L, 24000L);
		
		// Register Commands
		TopSurvivorCommandExecutor tscommandexec = new TopSurvivorCommandExecutor(this);
		this.getCommand("topsurvivor reset").setExecutor(tscommandexec);
		this.getCommand("topsurvivor view").setExecutor(tscommandexec);
		
	}
	
	/* Shutdown */
	
	@Override
	public void onDisable() {
		
		// Clean up players
		for(Player p: TopSurvivor.server.getOnlinePlayers()) {
			
			// Make sure time is recorded if player is afk when disabled
			
		}
		
	}
	
	
	/* Class Functions */
	
	
	
	/* Scoreboard */
	
	// Make Scoreboard
	public void makeScoreboard() {
		
		// Grab current main scoreboard
		tsmanager = TopSurvivor.server.getScoreboardManager();
		tsboard = tsmanager.getMainScoreboard();
		
		// Check to see if Objectives exist and store them. If not, initiate them
		if((afktimeobjective = tsboard.getObjective("afktime")) == null){
			afktimeobjective = tsboard.registerNewObjective("afktime", "dummy");
		}
		if((survivortimeobjective = tsboard.getObjective("survivortime")) == null){
			survivortimeobjective = tsboard.registerNewObjective("survivortime", "dummy");
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
	
}
