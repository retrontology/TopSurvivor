package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class TopSurvivorTask
  extends BukkitRunnable
{
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
