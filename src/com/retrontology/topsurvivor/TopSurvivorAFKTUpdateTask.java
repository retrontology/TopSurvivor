package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TopSurvivorAFKTUpdateTask extends BukkitRunnable{
	
	private TopSurvivor plugin;
	private TopSurvivorAFKTUpdate tsafktupdate = new TopSurvivorAFKTUpdate();
	
	public TopSurvivorAFKTUpdateTask (TopSurvivor plugin){
		this.plugin = plugin;
	}
	
	// Update SurvivorTimeObjective
	public void run() {
		Bukkit.getPluginManager().callEvent(tsafktupdate);
	}
}
