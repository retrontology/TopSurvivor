package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;

public class TopSurvivorDisplayTask 
implements Runnable
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
