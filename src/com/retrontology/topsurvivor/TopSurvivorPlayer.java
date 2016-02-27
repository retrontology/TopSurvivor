package com.retrontology.topsurvivor;

import java.io.File;
import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TopSurvivorPlayer {
	
	/* Class Variables */
	private File file;
	private FileConfiguration config = null;
	private OfflinePlayer player;
	private TopSurvivor plugin;

	/* Constructor */
	public TopSurvivorPlayer(String playername, TopSurvivor plugin) {
		// Set Class Variables
		this.player = plugin.server.getOfflinePlayer(playername);
		this.plugin = plugin;
		// Load player file into memory or create and init it if there isn't one
		file = new File("plugins"+File.separator+"PluginName"+File.separator+"Players"+File.separator+player.getName()+".yml");
		if(!file.exists()){
		    try {
		        file.createNewFile();
		        plugin.server.getLogger().info("[Top Survivor] File Created: "+ player.getName() + ".yml");
		        
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		 
		    config = YamlConfiguration.loadConfiguration(file);
		    config.set("Player.Name", player.getName());
		    config.set("Current.AfkTime", 0);			// Ticks
		    config.set("Current.AfkTPenalty", 0);		// Ticks
		    config.set("Flag.Exempt", false);			// Flag
		    config.set("Flag.New", true);				// Flag
		    config.set("Top.Tick", 0);					// Ticks
		    config.set("Top.AfkTime", 0);				// Ticks
		 
		    try {
		        config.save(file);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	/* Class Methods */
	
	// Get player info
	public int getCurrentAfkTime(){
		return config.getInt("Current.AfkTime");
	}
	
	public int getCurrentAfkTPenalty(){
		return config.getInt("Current.AfkTPenalty");
	}
	
	public boolean getFlagExempt(){
		return config.getBoolean("Flag.Exempt");
	}
	
	public int getTopTick(){
		return config.getInt("Top.Tick");
	}
	
	public int getTopAfkTime(){
		return config.getInt("Top.AfkTime");
	}
	
	public boolean getFlagNew(){
		return config.getBoolean("Flag.New");
	}
	
	// Set player info
	public int setCurrentAfkTime(int i){
		config.set("Current.AfkTime", i);
		return i;
	}
	
	public int setCurrentAfkTPenalty(int i){
		config.set("Current.AfkTPenalty", i);
		return i;
	}
	
	public boolean setFlagExempt(boolean i){
		config.set("Flag.Exempt", i);
		return i;
	}
	
	public boolean setFlagNew(boolean i){
		config.set("Flag.New", i);
		return i;
	}
	
	public int setTopTick(int i){
		config.set("Top.Tick", i);
		return i;
	}
	
	public int setTopAfkTime(int i){
		config.set("Top.AfkTime", i);
		return i;
	}
	
	// Reset player stats
	public void reset(){
		config.set("Current.AfkTime", 0);
	    config.set("Current.AfkTPenalty", 0);
	    config.set("Flag.Exempt", false);
	    config.set("Flag.New", false);
	    config.set("Top.Tick", 0);
	    config.set("Top.AfkTime", 0);
	}
	
	// Save player to disk
	public void save(){
		try {
	        config.save(file);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
}
