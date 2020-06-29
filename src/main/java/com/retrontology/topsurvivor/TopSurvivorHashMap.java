package com.retrontology.topsurvivor;

import com.earth2me.essentials.IUser;
import java.util.HashMap;
import java.util.UUID;
import net.ess3.api.events.AfkStatusChangeEvent;
import com.github.aasmus.pvptoggle.PvPToggle;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class TopSurvivorHashMap
{
  private TopSurvivor plugin;
  public static HashMap<String, Integer> timestampmap = new HashMap();
  public static HashMap<String, TopSurvivorPlayer> tsplayers = new HashMap();
  public static HashMap<String, Integer> gamemodemap = new HashMap();
  public static HashMap<String, Integer> pvpmap = new HashMap();
  
  public TopSurvivorHashMap(TopSurvivor plugin)
  {
    this.plugin = plugin;
  }
  
  public void onChangeEssentialsAfk(AfkStatusChangeEvent event)
  {
    IUser user = event.getAffected();
    Player player = TopSurvivor.server.getPlayer(user.getName());
    TopSurvivorPlayer tsplayer = (TopSurvivorPlayer)tsplayers.get(player.getName());
    if (!user.isAfk())
    {
      timestampmap.put(player.getName(), Integer.valueOf(TopSurvivor.timesincedeathobjective.getScore(player).getScore()));
    }
    else if (timestampmap.get(user.getName()) != null)
    {
      Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
      int timestamp = ((Integer)timestampmap.get(player.getName())).intValue();
      tsplayer.setCurrentAfkTime(tsplayer.getCurrentAfkTime() + (timesincedeathscore.getScore() - timestamp));
      
      timestampmap.remove(user.getName());
    }
  }
  
  public void onGameModeChange(Player player, GameMode gm)
  {
	  if(gm == GameMode.SURVIVAL)
	  {
		  if(gamemodemap.get(player.getName()) != null)
		  {
			  TopSurvivor.timesincedeathobjective.getScore(player).setScore(((Integer)gamemodemap.remove(player.getName())).intValue());
		  }
	  }
	  else
	  {
		  if(gamemodemap.get(player.getName()) == null)
		  {
			  gamemodemap.put(player.getName(), Integer.valueOf(TopSurvivor.timesincedeathobjective.getScore(player).getScore()));
		  }
	  }
  }

  public void onPVPChange(Player player, boolean pvp)
  {
    if(pvp == true)
    {
      if(pvpmap.get(player.getName()) != null)
      {
        TopSurvivor.timesincedeathobjective.getScore(player).setScore(((Integer)pvpmap.remove(player.getName())).intValue());
      }
    }
    else
    {
      if(pvpmap.get(player.getName()) == null)
      {
        pvpmap.put(player.getName(), Integer.valueOf(TopSurvivor.timesincedeathobjective.getScore(player).getScore()));
      }
    }
  }
  
  public void onLeave(Player player)
  {
    TopSurvivorPlayer tsplayer = (TopSurvivorPlayer)tsplayers.get(player.getName());
    if (timestampmap.get(player.getName()) != null)
    {
      Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
      int timestamp = ((Integer)timestampmap.get(player.getName())).intValue();
      tsplayer.setCurrentAfkTime(tsplayer.getCurrentAfkTime() + (timesincedeathscore.getScore() - timestamp));
      
      timestampmap.remove(player.getName());
    }
    if (gamemodemap.get(player.getName()) != null)
    {
      TopSurvivor.timesincedeathobjective.getScore(player).setScore(((Integer)gamemodemap.remove(player.getName())).intValue());
    }
    if (pvpmap.get(player.getName()) != null)
    {
      TopSurvivor.timesincedeathobjective.getScore(player).setScore(((Integer)pvpmap.remove(player.getName())).intValue());
    }
    removeTopSurvivorPlayer(player);
  }
  
  public void onDeath(Player player)
  {
    TopSurvivorPlayer tsplayer = (TopSurvivorPlayer)tsplayers.get(player.getName());
    if (timestampmap.get(player.getName()) != null)
    {
      Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
      int timestamp = ((Integer)timestampmap.get(player.getName())).intValue();
      tsplayer.setCurrentAfkTime(tsplayer.getCurrentAfkTime() + (timesincedeathscore.getScore() - timestamp));
      
      timestampmap.remove(player.getName());
    }
    if (gamemodemap.get(player.getName()) != null)
    {
      TopSurvivor.timesincedeathobjective.getScore(player).setScore(((Integer)gamemodemap.remove(player.getName())).intValue());
    }
    if (pvpmap.get(player.getName()) != null)
    {
      TopSurvivor.timesincedeathobjective.getScore(player).setScore(((Integer)pvpmap.remove(player.getName())).intValue());
    }
  }
  
  public void onRefresh(OfflinePlayer player)
  {
    TopSurvivorPlayer tsplayer = (TopSurvivorPlayer)tsplayers.get(player.getName());
    if (timestampmap.get(player.getName()) != null)
    {
      Score timesincedeathscore = TopSurvivor.timesincedeathobjective.getScore(player);
      int timestamp = ((Integer)timestampmap.get(player.getName())).intValue();
      tsplayer.setCurrentAfkTime(tsplayer.getCurrentAfkTime() + (timesincedeathscore.getScore() - timestamp));
      
      timestampmap.put(player.getName(), Integer.valueOf(timesincedeathscore.getScore()));
    }
  }
  
  public TopSurvivorPlayer getTopSurvivorPlayer(Player player)
  {
    if (tsplayers.get(player.getName()) == null) {
      tsplayers.put(player.getName(), new TopSurvivorPlayer(player.getName(), this.plugin));
    }
    return (TopSurvivorPlayer)tsplayers.get(player.getName());
  }
  
  public TopSurvivorPlayer getTopSurvivorPlayer(OfflinePlayer player)
  {
    if (tsplayers.get(player.getName()) == null) {
      tsplayers.put(player.getName(), new TopSurvivorPlayer(player.getName(), this.plugin));
    }
    return (TopSurvivorPlayer)tsplayers.get(player.getName());
  }
  
  public TopSurvivorPlayer getTopSurvivorPlayer(String player)
  {
    if (tsplayers.get(player) == null) {
      tsplayers.put(player, new TopSurvivorPlayer(player, this.plugin));
    }
    return (TopSurvivorPlayer)tsplayers.get(player);
  }
  
  public TopSurvivorPlayer getTopSurvivorPlayer(UUID player)
  {
    OfflinePlayer offplayer = this.plugin.getServer().getOfflinePlayer(player);
    if (offplayer.getFirstPlayed() != 0L)
    {
      if (tsplayers.get(offplayer.getName()) == null) {
        tsplayers.put(offplayer.getName(), new TopSurvivorPlayer(offplayer.getName(), this.plugin));
      }
      return (TopSurvivorPlayer)tsplayers.get(offplayer.getName());
    }
    return null;
  }
  
  public TopSurvivorPlayer removeTopSurvivorPlayer(Player player)
  {
    return (TopSurvivorPlayer)tsplayers.remove(player.getName());
  }
  
  public TopSurvivorPlayer removeTopSurvivorPlayer(OfflinePlayer player)
  {
    return (TopSurvivorPlayer)tsplayers.remove(player.getName());
  }
  
  public TopSurvivorPlayer removeTopSurvivorPlayer(String player)
  {
    return (TopSurvivorPlayer)tsplayers.remove(player);
  }
  
  public void deleteTopSurvivorPlayer(Player player)
  {
    ((TopSurvivorPlayer)tsplayers.get(player.getName())).delete();
    tsplayers.remove(player.getName());
  }
  
  public void deleteTopSurvivorPlayer(OfflinePlayer player)
  {
    ((TopSurvivorPlayer)tsplayers.get(player.getName())).delete();
    tsplayers.remove(player.getName());
  }
  
  public void deleteTopSurvivorPlayer(String player)
  {
    ((TopSurvivorPlayer)tsplayers.get(player)).delete();
    tsplayers.remove(player);
  }
}
