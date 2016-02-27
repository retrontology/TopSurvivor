package com.retrontology.topsurvivor;

import java.io.File;
import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TopSurvivorPlayer {
	
	/* Class Variables */
	private FileConfiguration config = null;
	private OfflinePlayer player;
	private TopSurvivor plugin;

	/* Constructor */
	public TopSurvivorPlayer(String playername, TopSurvivor plugin) {
		// Set Class Variables
		this.player = plugin.server.getOfflinePlayer(playername);
		this.plugin = plugin;
		// Load player file into memory or create and init it if there isn't one
		File file = new File("plugins"+File.separator+"PluginName"+File.separator+"Players"+File.separator+player.getName()+".yml");
		if(!file.exists()){
		    try {
		        file.createNewFile();
		        plugin.server.getLogger().info("[Top Survivor] File Created: "+ player.getName() + ".yml");
		        
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		 
		    config = YamlConfiguration.loadConfiguration(file);
		    config.set("Player.Name", player.getName());
		    config.set("Current.AfkTime", 0);
		    config.set("Current.AfkTPenalty", 0);
		    config.set("Flag.Exempt", false);
		    config.set("Top.Tick", 0);
		    config.set("Top.AfkTime", 0);
		 
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
	
	// Set player info
	public void setCurrentAfkTime(int i){
		config.set("Current.AfkTime", i);
	}
	
	public void setCurrentAfkTPenalty(int i){
		config.set("Current.AfkTPenalty", i);
	}
	
	public void setFlagExempt(boolean i){
		config.set("Flag.Exempt", i);
	}
	
	public void setTopTick(int i){
		config.set("Top.Tick", i);
	}
	
	public void setTopAfkTime(int i){
		config.set("Top.AfkTime", i);
	}
	
	// Reset player stats
	public void reset(){
		config.set("Current.AfkTime", 0);
	    config.set("Current.AfkTPenalty", 0);
	    config.set("Flag.Exempt", false);
	    config.set("Top.Tick", 0);
	    config.set("Top.AfkTime", 0);
	}
	
}
