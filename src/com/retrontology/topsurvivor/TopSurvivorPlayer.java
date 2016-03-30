package com.retrontology.topsurvivor;

import java.io.File;
import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class TopSurvivorPlayer {
	
	/* Class Variables */
	private File file;
	private FileConfiguration config;
	private String player;
	private TopSurvivor plugin;
	private OfflinePlayer offplayer;

	/* Constructor */
	public TopSurvivorPlayer(String playername, TopSurvivor plugin) {
		// Set Class Variables
		this.player = playername;
		this.plugin = plugin;
		this.offplayer = this.plugin.getServer().getOfflinePlayer(this.player);
		
		// Init directory if it's not there
		File filedir = new File(plugin.server.getPluginManager().getPlugin("TopSurvivor").getDataFolder(), File.separator+"Players");
        if (!filedir.exists()) { filedir.mkdir(); }
        // Load player file into memory or, create and init it if there isn't one
		file = new File(filedir, File.separator+offplayer.getUniqueId().toString()+".yml");
		if(!file.exists()){
		    try {
		        file.createNewFile();
		        plugin.server.getLogger().info("[Top Survivor] File Created: "+ player + ".yml");
		        
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		config = YamlConfiguration.loadConfiguration(file);
		if(!config.contains("Player.Name")){ config.set("Player.Name", player); }
		if(!config.contains("Current.AfkTime")){ config.set("Current.AfkTime", 0); }			// Ticks
		if(!config.contains("Current.AfkTPenalty")){ config.set("Current.AfkTPenalty", 0); }	// Ticks
		if(!config.contains("Flag.Exempt")){ config.set("Flag.Exempt", false); }				// Flag
		if(!config.contains("Flag.New")){ config.set("Flag.New", true); }						// Flag
		if(!config.contains("Flag.Perma")){ config.set("Flag.Perma", false); }					// Flag
		if(!config.contains("Top.Tick")){ config.set("Top.Tick", 0); }							// Ticks
		if(!config.contains("Top.AfkTime")){ config.set("Top.AfkTime", 0); }					// Ticks
		if(!config.contains("Total.PlayerKills")){ config.set("Total.PlayerKills", 0); }		// Count
		if(!config.contains("Total.Deaths")){ config.set("Total.Deaths", 0); }					// Count
		if(!config.contains("Total.AFKTime")){ config.set("Total.AFKTime", 0); }				// Ticks
		if(!config.contains("Ban.Length")){ config.set("Ban.Length", 0); }						// Ticks
		if(!config.contains("Last.Death")){ config.set("Last.Death", 0); }						// Milliseconds
		save();
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
	
	public Integer getTotalPlayerKills(){
		return config.getInt("Total.PlayerKills");
	}
	
	public Integer getTotalDeaths(){
		return config.getInt("Total.Deaths");
	}
	
	public int getSurvivorTime(){
		return TimeConverter.getDays(this.getTopTick() - this.getTopAfkTime() - this.getCurrentAfkTPenalty());
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
	
	public Long getLastDeath(){
		return config.getLong("Last.Death");
	}
	
	public int getTotalAfkTime(){
		return config.getInt("Total.AFKTime");
	}
	
	public int getBanLength(){
		return config.getInt("Ban.Length");
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
	
	public int setTotalPlayerKills(int i){
		config.set("Total.PlayerKills", i);
		save();
		return i;
	}
	
	public int setTotalDeaths(int i){
		config.set("Total.Deaths", i);
		save();
		return i;
	}
	
	public long setLastDeath(long l){
		config.set("Last.Death", l);
		save();
		return l;
	}
	
	public String setPlayerName(String s){
		config.set("Player.Name", s);
		save();
		return s;
	}
	
	public int setTotalAfkTime(int i){
		config.set("Total.AFKTime", i);
		save();
		return i;
	}
	
	public int setBanLength(int i){
		config.set("Ban.Length", i);
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
