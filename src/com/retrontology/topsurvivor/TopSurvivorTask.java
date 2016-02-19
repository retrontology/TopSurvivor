package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TopSurvivorTask extends BukkitRunnable{
	
	private TopSurvivor plugin;
	private TopSurvivorUpdate tsupdate;
	
	public TopSurvivorTask (TopSurvivor plugin){
		this.plugin = plugin;
		TopSurvivorUpdate tsupdate = new TopSurvivorUpdate();
	}
	
	// Update SurvivorTimeObjective
	public void run() {
		Bukkit.getPluginManager().callEvent(tsupdate);
	}

}
