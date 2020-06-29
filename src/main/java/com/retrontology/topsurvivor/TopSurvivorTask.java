package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;

public class TopSurvivorTask
        implements Runnable {
  private TopSurvivor plugin;
  private TopSurvivorUpdate tsupdate = new TopSurvivorUpdate();
  
  public TopSurvivorTask(TopSurvivor plugin)
  {
    this.plugin = plugin;
  }
  
  public void run()
  {
    Bukkit.getPluginManager().callEvent(this.tsupdate);
  }
}
