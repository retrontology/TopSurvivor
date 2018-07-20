package com.retrontology.topsurvivor;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class TopSurvivorPlayer
{
  private File file;
  private FileConfiguration config;
  private String player;
  private TopSurvivor plugin;
  private OfflinePlayer offplayer;
  
  public TopSurvivorPlayer(String playername, TopSurvivor plugin)
  {
    this.player = playername;
    this.plugin = plugin;
    this.offplayer = this.plugin.getServer().getOfflinePlayer(this.player);
    
    File filedir = new File(TopSurvivor.server.getPluginManager().getPlugin("TopSurvivor").getDataFolder(), File.separator + "Players");
    if (!filedir.exists()) {
      filedir.mkdir();
    }
    this.file = new File(filedir, File.separator + this.offplayer.getUniqueId().toString() + ".yml");
    if (!this.file.exists()) {
      try
      {
        this.file.createNewFile();
        TopSurvivor.server.getLogger().info("[Top Survivor] File Created: " + this.player + ".yml");
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    this.config = YamlConfiguration.loadConfiguration(this.file);
    if (!this.config.contains("Player.Name")) {
      this.config.set("Player.Name", this.player);
    }
    if (!this.config.contains("Current.AfkTime")) {
      this.config.set("Current.AfkTime", Integer.valueOf(0));
    }
    if (!this.config.contains("Current.AfkTPenalty")) {
      this.config.set("Current.AfkTPenalty", Integer.valueOf(0));
    }
    if (!this.config.contains("Flag.Exempt")) {
      this.config.set("Flag.Exempt", Boolean.valueOf(false));
    }
    if (!this.config.contains("Flag.New")) {
      this.config.set("Flag.New", Boolean.valueOf(true));
    }
    if (!this.config.contains("Flag.Perma")) {
      this.config.set("Flag.Perma", Boolean.valueOf(false));
    }
    if (!this.config.contains("Top.Tick")) {
      this.config.set("Top.Tick", Integer.valueOf(0));
    }
    if (!this.config.contains("Top.AfkTime")) {
      this.config.set("Top.AfkTime", Integer.valueOf(0));
    }
    if (!this.config.contains("Total.PlayerKills")) {
      this.config.set("Total.PlayerKills", Integer.valueOf(0));
    }
    if (!this.config.contains("Total.Deaths")) {
      this.config.set("Total.Deaths", Integer.valueOf(0));
    }
    if (!this.config.contains("Total.AFKTime")) {
      this.config.set("Total.AFKTime", Integer.valueOf(0));
    }
    if (!this.config.contains("Ban.Length")) {
      this.config.set("Ban.Length", Integer.valueOf(0));
    }
    if (!this.config.contains("Last.Death")) {
      this.config.set("Last.Death", Integer.valueOf(0));
    }
    if (!this.config.contains("Last.SurvivalTick")) {
        this.config.set("Last.SurvivalTick", Long.valueOf(0));
    }
    save();
  }
  
  public int getCurrentAfkTime()
  {
    return this.config.getInt("Current.AfkTime");
  }
  
  public int getCurrentAfkTPenalty()
  {
    return this.config.getInt("Current.AfkTPenalty");
  }
  
  public boolean getFlagExempt()
  {
    return this.config.getBoolean("Flag.Exempt");
  }
  
  public int getTopTick()
  {
    return this.config.getInt("Top.Tick");
  }
  
  public int getTopAfkTime()
  {
    return this.config.getInt("Top.AfkTime");
  }
  
  public Integer getTotalPlayerKills()
  {
    return Integer.valueOf(this.config.getInt("Total.PlayerKills"));
  }
  
  public Integer getTotalDeaths()
  {
    return Integer.valueOf(this.config.getInt("Total.Deaths"));
  }
  
  public int getSurvivorTime()
  {
    return TimeConverter.getDays(getTopTick() - getTopAfkTime() - getCurrentAfkTPenalty());
  }
  
  public boolean getFlagNew()
  {
    return this.config.getBoolean("Flag.New");
  }
  
  public boolean getFlagPermaban()
  {
    return this.config.getBoolean("Flag.Perma");
  }
  
  public String getPlayerName()
  {
    return this.player;
  }
  
  public Long getLastDeath()
  {
    return Long.valueOf(this.config.getLong("Last.Death"));
  }
  
  public int getTotalAfkTime()
  {
    return this.config.getInt("Total.AFKTime");
  }
  
  public int getBanLength()
  {
    return this.config.getInt("Ban.Length");
  }
  
  public long getLastSurvivalTick()
  {
    return this.config.getLong("Last.SurvivalTick");
  }
  
  public int setCurrentAfkTime(int i)
  {
    this.config.set("Current.AfkTime", Integer.valueOf(i));
    save();
    return i;
  }
  
  public int setCurrentAfkTPenalty(int i)
  {
    this.config.set("Current.AfkTPenalty", Integer.valueOf(i));
    save();
    return i;
  }
  
  public boolean setFlagExempt(boolean i)
  {
    this.config.set("Flag.Exempt", Boolean.valueOf(i));
    save();
    return i;
  }
  
  public boolean setFlagNew(boolean i)
  {
    this.config.set("Flag.New", Boolean.valueOf(i));
    save();
    return i;
  }
  
  public boolean setFlagPermaban(boolean i)
  {
    this.config.set("Flag.Perma", Boolean.valueOf(i));
    save();
    return i;
  }
  
  public int setTopTick(int i)
  {
    this.config.set("Top.Tick", Integer.valueOf(i));
    save();
    return i;
  }
  
  public int setTopAfkTime(int i)
  {
    this.config.set("Top.AfkTime", Integer.valueOf(i));
    save();
    return i;
  }
  
  public int setTotalPlayerKills(int i)
  {
    this.config.set("Total.PlayerKills", Integer.valueOf(i));
    save();
    return i;
  }
  
  public int setTotalDeaths(int i)
  {
    this.config.set("Total.Deaths", Integer.valueOf(i));
    save();
    return i;
  }
  
  public long setLastDeath(long l)
  {
    this.config.set("Last.Death", Long.valueOf(l));
    save();
    return l;
  }
  
  public String setPlayerName(String s)
  {
    this.config.set("Player.Name", s);
    save();
    return s;
  }
  
  public int setTotalAfkTime(int i)
  {
    this.config.set("Total.AFKTime", Integer.valueOf(i));
    save();
    return i;
  }
  
  public int setBanLength(int i)
  {
    this.config.set("Ban.Length", Integer.valueOf(i));
    save();
    return i;
  }
  
  public long setLastSurvivalTick(long l)
  {
	this.config.set("Last.SurvivalTick", Long.valueOf(l));
	save();
    return l;
  }
  
  public void reset()
  {
    this.config.set("Current.AfkTime", Integer.valueOf(0));
    this.config.set("Current.AfkTPenalty", Integer.valueOf(0));
    this.config.set("Flag.Exempt", Boolean.valueOf(false));
    this.config.set("Flag.New", Boolean.valueOf(false));
    this.config.set("Top.Tick", Integer.valueOf(0));
    this.config.set("Top.AfkTime", Integer.valueOf(0));
    save();
  }
  
  public boolean save()
  {
    try
    {
      this.config.save(this.file);
      return true;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return false;
  }
  
  public void delete()
  {
    try
    {
      this.file.delete();
    }
    catch (SecurityException e)
    {
      e.printStackTrace();
    }
  }
}
