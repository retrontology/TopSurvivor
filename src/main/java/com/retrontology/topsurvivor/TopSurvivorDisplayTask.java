package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TopSurvivorDisplayTask 
extends BukkitRunnable
{
	  private TopSurvivor plugin;
	  private TopSurvivorDisplay tsdisplay = new TopSurvivorDisplay();
	  
	  public TopSurvivorDisplayTask(TopSurvivor plugin)
	  {
	    this.plugin = plugin;
	  }
	  
	  public void run()
	  {
	    Bukkit.getPluginManager().callEvent(this.tsdisplay);
	  }
}
