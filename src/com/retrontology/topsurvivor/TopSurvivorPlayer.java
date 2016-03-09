package com.retrontology.topsurvivor;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class TopSurvivorPlayer {
	
	/* Class Variables */
	private File file;
	private FileConfiguration config;
	private String player;
	private TopSurvivor plugin;

	/* Constructor */
	public TopSurvivorPlayer(String playername, TopSurvivor plugin) {
		// Set Class Variables
		this.player = playername;
		this.plugin = plugin;
		// Init directory if it's not there
		File filedir = new File(plugin.server.getPluginManager().getPlugin("TopSurvivor").getDataFolder(), File.separator+"Players");
        if (!filedir.exists()) { filedir.mkdir(); }
        // Load player file into memory or, create and init it if there isn't one
		file = new File(filedir, File.separator+player+".yml");
		if(!file.exists()){
		    try {
		        file.createNewFile();
		        plugin.server.getLogger().info("[Top Survivor] File Created: "+ player + ".yml");
		        
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		 
		    config = YamlConfiguration.loadConfiguration(file);
		    config.set("Player.Name", player);
		    config.set("Current.AfkTime", 0);			// Ticks
		    config.set("Current.AfkTPenalty", 0);		// Ticks
		    config.set("Flag.Exempt", false);			// Flag
		    config.set("Flag.New", true);				// Flag
		    config.set("Flag.Perma", false);			// Flag
		    config.set("Top.Tick", 0);					// Ticks
		    config.set("Top.AfkTime", 0);				// Ticks
		 
		    try {
		        config.save(file);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}else{
			config = YamlConfiguration.loadConfiguration(file);
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
	
	public boolean getFlagPermaban(){
		return config.getBoolean("Flag.Perma");
	}
	
	public String getPlayerName(){
		return player;
	}
	
	// Set player info
	public int setCurrentAfkTime(int i){
		config.set("Current.AfkTime", i);
		save();
		return i;
	}
	
	public int setCurrentAfkTPenalty(int i){
		config.set("Current.AfkTPenalty", i);
		save();
		return i;
	}
	
	public boolean setFlagExempt(boolean i){
		config.set("Flag.Exempt", i);
		save();
		return i;
	}
	
	public boolean setFlagNew(boolean i){
		config.set("Flag.New", i);
		save();
		return i;
	}
	
	public boolean setFlagPermaban( boolean i){
		config.set("Flag.Perma", i);
		save();
		return i;
	}
	
	public int setTopTick(int i){
		config.set("Top.Tick", i);
		save();
		return i;
	}
	
	public int setTopAfkTime(int i){
		config.set("Top.AfkTime", i);
		save();
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
	    save();
	}
	
	// Save player to disk
	public boolean save(){
		try {
	        config.save(file);
	        return true;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// Delete player file
	public void delete(){
		try {
			file.delete();
		} catch (SecurityException e) {
		    // File permission problems are caught here.
		    e.printStackTrace();
		}
	}
	
}
